package com.kehua.energy.monitor.app.model.local;

import android.util.ArrayMap;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.model.entity.DeviceData_;
import com.kehua.energy.monitor.app.model.entity.GroupInfo;
import com.kehua.energy.monitor.app.model.entity.GroupInfo_;
import com.kehua.energy.monitor.app.model.entity.LocalAlarmList;
import com.kehua.energy.monitor.app.model.entity.PointInfo;
import com.kehua.energy.monitor.app.model.entity.PointInfo_;
import com.kehua.energy.monitor.app.model.entity.RecordData;
import com.kehua.energy.monitor.app.model.entity.RecordPoint;
import com.kehua.energy.monitor.app.model.entity.RecordPoint_;
import com.kehua.energy.monitor.app.model.entity.SGroupInfo;
import com.kehua.energy.monitor.app.model.local.db.ObjectBox;
import com.kehua.energy.monitor.app.utils.ByteUtils;
import com.kehua.energy.monitor.app.utils.Utils;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import me.walten.fastgo.base.mvp.BaseModel;
import me.walten.fastgo.base.mvp.IModel;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.utils.XToast;

public class LocalModel extends BaseModel implements IModel {

    @Inject
    public LocalModel() {
        super(null);
    }

    /**
     * 数据库是否初始化成功
     *
     * @return
     */
    public boolean isDBInited() {
        long count = ObjectBox.get().getBoxStore().boxFor(PointInfo.class).count();
        return count != 0;
    }

    public List<DeviceData> parse(String sn, byte[] data, int start, int end, int protocol, Date updateTime) {
        return parse(sn, data, start, end, protocol, updateTime, true);
    }

    /**
     * 解析数据
     *
     * @param sn
     * @param data
     * @param start
     * @param end
     * @param protocol
     * @param updateTime
     */
    public List<DeviceData> parse(String sn, byte[] data, int start, int end, int protocol, Date updateTime, boolean popCache) {
        //int end = start + len;
        List<DeviceData> resultList = new ArrayList<>();
        while (start <= end) {
            long count = ObjectBox.get().getBoxStore().boxFor(PointInfo.class).query()
                    .equal(PointInfo_.pn, String.valueOf(protocol))
                    .equal(PointInfo_.address, String.valueOf(start)).build().count();
            if (count == 0) {
                start += 1;

                if (start <= end) {
                    //移除无法解析的字节
                    data = ByteUtils.subArray(data, 2);
                }

                continue;
            }
            //寄存器映射信息
            PointInfo info = ObjectBox.get().getBoxStore().boxFor(PointInfo.class).query()
                    .equal(PointInfo_.pn, String.valueOf(protocol))
                    .equal(PointInfo_.address, String.valueOf(start)).build().findFirst();

            //
            byte[] temp = new byte[info.getByteCount()];

            System.arraycopy(data, 0, temp, 0, temp.length > data.length ? data.length : temp.length);

            if ("boolean".equals(info.getDataType()) || "boolean_int".equals(info.getDataType()) || "boolean_int_no_parse".equals(info.getDataType())) {
                //状态量

                List<DeviceData> list = new ArrayList<>();

                for (int i = 0; i < 8; i++) {
                    if (ObjectBox.get().getBoxStore().boxFor(PointInfo.class).query()
                            .equal(PointInfo_.pn, String.valueOf(protocol))
                            .equal(PointInfo_.address, String.valueOf(start + i)).build().count() > 0) {

                        PointInfo tempInfo = ObjectBox.get().getBoxStore().boxFor(PointInfo.class).query()
                                .equal(PointInfo_.pn, String.valueOf(protocol))
                                .equal(PointInfo_.address, String.valueOf(start + i)).build().findFirst();

                        if (tempInfo == null)
                            continue;

                        DeviceData deviceData = new DeviceData(sn,
                                String.valueOf(start + i),
                                DeviceData.TYPE_STATUS,
                                tempInfo.getDescriptionCN(),
                                tempInfo.getDescriptionEN(),
                                tempInfo.getDescriptionFrench(),
                                (temp[0] >> (i)) & 0x1,
                                String.valueOf((temp[0] >> (i)) & 0x1),
                                tempInfo.getUnit(),
                                updateTime,
                                tempInfo.getGroup(),
                                tempInfo.getSgroup(),
                                tempInfo.getAuthority(),
                                tempInfo.getDataType(),
                                tempInfo.getAccuracy(),
                                tempInfo.getByteCount());

                        if (popCache)
                            CacheManager.getInstance().put(Integer.valueOf(deviceData.getRegisterAddress()), deviceData);
                        list.add(deviceData);
                    }
                }

                resultList.addAll(resultList.size(), list);

                start += 8;

                if (start <= end) {
                    //移除已处理字节
                    data = ByteUtils.subArray(data, info.getByteCount());
                }
                continue;
            } else if ("int".equals(info.getDataType())) {
                if (start >= 6027 && start <= 6051 && start != 6039) {
                    //充放电时段特殊解析
                    byte[] highByte = new byte[4];
                    highByte[3] = temp[0];

                    byte[] lowByte = new byte[4];
                    lowByte[3] = temp[1];

                    int hour = ByteUtils.bytes2Int(highByte);
                    int minute = ByteUtils.bytes2Int(lowByte);
                    String hourStr = hour < 10 ? "0" + hour : "" + hour;
                    String minuteStr = minute < 10 ? "0" + minute : "" + minute;

                    DeviceData deviceData = new DeviceData(sn,
                            String.valueOf(start),
                            DeviceData.TYPE_HOLD,
                            info.getDescriptionCN(),
                            info.getDescriptionEN(),
                            info.getDescriptionFrench(),
                            ByteUtils.bytes2Int(temp),
                            String.valueOf(hourStr + ":" + minuteStr),
                            info.getUnit(),
                            updateTime,
                            info.getGroup(),
                            info.getSgroup(),
                            info.getAuthority(),
                            info.getDataType(),
                            info.getAccuracy(),
                            info.getByteCount());
                    resultList.add(deviceData);
                    if (popCache)
                        CacheManager.getInstance().put(Integer.valueOf(deviceData.getRegisterAddress()), deviceData);
                } else {
                    int result = ByteUtils.bytes2Int(temp);

                    DeviceData deviceData = new DeviceData(sn,
                            String.valueOf(start),
                            DeviceData.TYPE_HOLD,
                            info.getDescriptionCN(),
                            info.getDescriptionEN(),
                            info.getDescriptionFrench(),
                            result,
                            String.valueOf(result),
                            info.getUnit(),
                            updateTime,
                            info.getGroup(),
                            info.getSgroup(),
                            info.getAuthority(),
                            info.getDataType(),
                            info.getAccuracy(),
                            info.getByteCount());
                    resultList.add(deviceData);
                    if (popCache)
                        CacheManager.getInstance().put(Integer.valueOf(deviceData.getRegisterAddress()), deviceData);
                }

            } else if ("double".equals(info.getDataType())) {
                int result = ByteUtils.bytes2Int(temp);

                String parseV;
                if (info.getAccuracy() == 0) {
                    parseV = String.valueOf(result);
                } else {
                    String pattern = "#0.";
                    for (int i = 0; i < info.getAccuracy(); i++) {
                        pattern += "0";
                    }
                    DecimalFormat format = new DecimalFormat(pattern);
                    parseV = format.format(result / Math.pow(10, info.getAccuracy()));
                }

                DeviceData deviceData = new DeviceData(sn,
                        String.valueOf(start),
                        DeviceData.TYPE_HOLD,
                        info.getDescriptionCN(),
                        info.getDescriptionEN(),
                        info.getDescriptionFrench(),
                        result,
                        String.valueOf(parseV),
                        info.getUnit(),
                        updateTime,
                        info.getGroup(),
                        info.getSgroup(),
                        info.getAuthority(),
                        info.getDataType(),
                        info.getAccuracy(),
                        info.getByteCount());
                resultList.add(deviceData);
                if (popCache)
                    CacheManager.getInstance().put(Integer.valueOf(deviceData.getRegisterAddress()), deviceData);
            } else if ("int_signed".equals(info.getDataType()) || "double_signed".equals(info.getDataType())) {
                //有符号
                int result = ByteUtils.bytes2SignedInt(temp);

                String parseV;
                if (info.getAccuracy() == 0) {
                    parseV = String.valueOf(result);
                } else {
                    String pattern = "#0.";
                    for (int i = 0; i < info.getAccuracy(); i++) {
                        pattern += "0";
                    }
                    DecimalFormat format = new DecimalFormat(pattern);
                    parseV = format.format(result / Math.pow(10, info.getAccuracy()));
                }

                DeviceData deviceData = new DeviceData(sn,
                        String.valueOf(start),
                        DeviceData.TYPE_HOLD,
                        info.getDescriptionCN(),
                        info.getDescriptionEN(),
                        info.getDescriptionFrench(),
                        result,
                        String.valueOf(parseV),
                        info.getUnit(),
                        updateTime,
                        info.getGroup(),
                        info.getSgroup(),
                        info.getAuthority(),
                        info.getDataType(),
                        info.getAccuracy(),
                        info.getByteCount());
                resultList.add(deviceData);
                if (popCache)
                    CacheManager.getInstance().put(Integer.valueOf(deviceData.getRegisterAddress()), deviceData);

            } else if ("string".equals(info.getDataType())) {
                String result = ByteUtils.bytes2String(temp);

                DeviceData deviceData = new DeviceData(sn,
                        String.valueOf(start),
                        DeviceData.TYPE_HOLD,
                        info.getDescriptionCN(),
                        info.getDescriptionEN(),
                        info.getDescriptionFrench(),
                        0,
                        String.valueOf(result.trim()),
                        info.getUnit(),
                        updateTime,
                        info.getGroup(),
                        info.getSgroup(),
                        info.getAuthority(),
                        info.getDataType(),
                        info.getAccuracy(),
                        info.getByteCount());

                resultList.add(deviceData);
                if (popCache)
                    CacheManager.getInstance().put(Integer.valueOf(deviceData.getRegisterAddress()), deviceData);
            } else {

                //不解析 保存原数据
                String result = ByteUtils.bytesToHexString(temp);

                DeviceData deviceData = new DeviceData(sn,
                        String.valueOf(start),
                        DeviceData.TYPE_HOLD,
                        info.getDescriptionCN(),
                        info.getDescriptionEN(),
                        info.getDescriptionFrench(),
                        0,
                        String.valueOf(result.trim()),
                        info.getUnit().trim(),
                        updateTime,
                        info.getGroup(),
                        info.getSgroup(),
                        info.getAuthority(),
                        info.getDataType(),
                        info.getAccuracy(),
                        info.getByteCount());

                resultList.add(deviceData);
                if (popCache)
                    CacheManager.getInstance().put(Integer.valueOf(deviceData.getRegisterAddress()), deviceData);

            }

            start += (info.getByteCount() / 2);

            if (start <= end) {
                //移除已处理字节
                data = ByteUtils.subArray(data, info.getByteCount());
            }

        }

        return resultList;

    }

    /**
     * 解析数据
     */
    public List<RecordData> parse(int deviceAddress, int template, byte[] data) {
        List<RecordData> result = new ArrayList<>();
        if (data.length % 12 != 0) {
            return result;
        }

        for (int count = 0; count < data.length / 12; count++) {
            byte[] tempByte = ByteUtils.subArray(data, count * 12);

            int emptyCount = 0;
            for (byte b : tempByte) {
                if (b == 0) {
                    emptyCount++;
                }
            }
            if (emptyCount == tempByte.length)
                return result;

            int[] timesVal = new int[6];
            String time = "";
            for (int i = 0; i < 6; i++) {
                byte[] newByte = new byte[4];
                newByte[3] = tempByte[i];

                int temp = ByteUtils.bytes2Int(newByte);
                timesVal[i] = temp;
            }
            time = (2000 + timesVal[0]) + "-"
                    + Utils.startWithZero(timesVal[1]) + "-"
                    + Utils.startWithZero(timesVal[2]) + " "
                    + Utils.startWithZero(timesVal[3]) + ":"
                    + Utils.startWithZero(timesVal[4]) + ":"
                    + Utils.startWithZero(timesVal[5]);

            int code = ByteUtils.bytes2Int(new byte[]{tempByte[6], tempByte[7]});

            long size = ObjectBox.get().getBoxStore().boxFor(RecordPoint.class).query()
                    .equal(RecordPoint_.code, code)
                    .equal(RecordPoint_.template, template).build().count();
            if (size == 0) {
                XToast.error(Fastgo.getContext().getString(R.string.映射模板错误));
                return result;
            }

            RecordPoint pointInfo = ObjectBox.get().getBoxStore().boxFor(RecordPoint.class).query()
                    .equal(RecordPoint_.code, code)
                    .equal(RecordPoint_.template, template).build().findFirst();

            int value;

            String parseValue = null;
            if (!StringUtils.isEmpty(pointInfo.getV0())) {
                value = ByteUtils.bytes2Int(new byte[]{tempByte[8], tempByte[9], tempByte[10], tempByte[11]});
                //解析0 跟 1
                if (value == 0) {
                    parseValue = pointInfo.getV0();
                } else if(value == 1){
                    parseValue = pointInfo.getV1();
                } else if(value == 2){
                    parseValue = pointInfo.getV2();
                }else if(value == 3){
                    parseValue = pointInfo.getV3();
                }
            } else {
                if (pointInfo.getSign() == 1) {
                    //有符号
                    value = ByteUtils.bytes2SignedInt(new byte[]{tempByte[8], tempByte[9], tempByte[10], tempByte[11]});
                } else {
                    //无符号
                    value = ByteUtils.bytes2Int(new byte[]{tempByte[8], tempByte[9], tempByte[10], tempByte[11]});
                }

                if (pointInfo.getAccuracy() == 0) {
                    parseValue = String.valueOf(value) + " " + pointInfo.getUnit();
                } else {
                    String pattern = "#0.";
                    for (int i = 0; i < pointInfo.getAccuracy(); i++) {
                        pattern += "0";
                    }
                    DecimalFormat format = new DecimalFormat(pattern);
                    parseValue = format.format(value / Math.pow(10, pointInfo.getAccuracy())) + " " + pointInfo.getUnit();
                }
            }

            result.add(new RecordData(deviceAddress, pointInfo.getMeansCN(), code, value, parseValue, time, !StringUtils.isEmpty(pointInfo.getV0())));
        }

        return result;
    }

    /**
     * 保存
     *
     * @param list
     */
    public void saveDeviceDataList(List<DeviceData> list) {
        ObjectBox.get().getBoxStore().boxFor(DeviceData.class).put(list);
    }

    /**
     * 根据数据类型删除
     *
     * @param type
     */
    public void removeDeviceDataWithType(int type) {
        List<DeviceData> deviceData = ObjectBox.get().getBoxStore().boxFor(DeviceData.class).query().equal(DeviceData_.type, type).build().find();
        ObjectBox.get().getBoxStore().boxFor(DeviceData.class).remove(deviceData);
    }

    /**
     * 查询实时数据
     *
     * @param sn
     * @param type
     * @param start
     * @param end
     * @return
     */
    public List<DeviceData> getDeviceDataList(String sn, int type, int start, int end) {
        QueryBuilder<DeviceData> query = ObjectBox.get().getBoxStore().boxFor(DeviceData.class).query();
        query.equal(DeviceData_.sn, sn);
        if (type != -1) {
            query.equal(DeviceData_.type, type);
        }

        if (start != -1 && end != -1) {
            query.between(DeviceData_.registerAddress, start, end);
        }

        return query.build().find();
    }

    public void setupDatabase() {

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (SPUtils.getInstance().getInt("app_version") != AppUtils.getAppVersionCode()) {
                    try {
                        ObjectBox.get().getBoxStore().boxFor(SGroupInfo.class).removeAll();
                        ObjectBox.get().getBoxStore().boxFor(GroupInfo.class).removeAll();
                        ObjectBox.get().getBoxStore().boxFor(PointInfo.class).removeAll();

                        setupPoint();

                        setupGroup();

                        setupRecord();

                        SPUtils.getInstance().put("app_version", AppUtils.getAppVersionCode());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (BiffException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Logger.d("数据库初始化完成");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.e("数据库初始化失败");
                    }
                });
    }

    private void setupPoint() throws IOException, BiffException {
        Workbook workbook = Workbook.getWorkbook(Fastgo.getContext().getAssets().open("POINT.xls"));
        //获得第一个工作表对象(ecxel中sheet的编号从0开始,0,1,2,3,....)
        Sheet sheet = workbook.getSheet(0);

        List<PointInfo> list = new ArrayList<>();
        for (int i = 1; i < sheet.getRows(); i++) {
            //获取每一行的单元格
            Cell PN = sheet.getCell(0, i);//（列，行）
            Cell ADDRESS = sheet.getCell(1, i);
            Cell DESCRIPTION_CN = sheet.getCell(2, i);
            Cell DESCRIPTION_EN = sheet.getCell(3, i);
            Cell DESCRIPTION_FRENCH = sheet.getCell(4, i);
            Cell BYTE_COUNT = sheet.getCell(5, i);
            Cell DATA_TYPE = sheet.getCell(6, i);
            Cell ACCURACY = sheet.getCell(7, i);
            Cell UNIT = sheet.getCell(8, i);
            Cell DEVICE_TYPE = sheet.getCell(9, i);
            Cell GROUP = sheet.getCell(10, i);
            Cell SORT = sheet.getCell(11, i);
            Cell AUTHORITY = sheet.getCell(12, i);

            if ("".equals(PN.getContents())) {//如果读取的数据为空
                break;
            }
            list.add(new PointInfo(
                    PN.getContents(),
                    ADDRESS.getContents(),
                    DESCRIPTION_CN.getContents(),
                    DESCRIPTION_EN.getContents(),
                    DESCRIPTION_FRENCH.getContents(),
                    Integer.valueOf(BYTE_COUNT.getContents()),
                    DATA_TYPE.getContents(),
                    Integer.valueOf(ACCURACY.getContents()),
                    UNIT.getContents().trim(),
                    Integer.valueOf(DEVICE_TYPE.getContents()),
                    GROUP.getContents(),
                    SORT.getContents(),
                    AUTHORITY.getContents()
            ));

        }

        ObjectBox.get().getBoxStore().boxFor(PointInfo.class).put(list);
        workbook.close();
    }

    private void setupGroup() throws IOException, BiffException {
        Workbook workbook = Workbook.getWorkbook(Fastgo.getContext().getAssets().open("GROUP.xls"));
        //获得第一个工作表对象(ecxel中sheet的编号从0开始,0,1,2,3,....)
        Sheet sheet = workbook.getSheet(0);

        List<SGroupInfo> sGroupInfos = new ArrayList<>();
        List<GroupInfo> groupInfos = new ArrayList<>();
        ArrayMap<String, String> only = new ArrayMap<>();
        for (int i = 1; i < sheet.getRows(); i++) {
            //获取每一行的单元格
            Cell PN = sheet.getCell(0, i);//（列，行）
            Cell GROUP = sheet.getCell(1, i);
            Cell GROUP_NAME_CN = sheet.getCell(2, i);
            Cell GROUP_NAME_EN = sheet.getCell(3, i);
            Cell GROUP_NAME_FRENCH = sheet.getCell(4, i);

            Cell SGROUP = sheet.getCell(5, i);
            Cell SGROUP_NAME_CN = sheet.getCell(6, i);
            Cell SGROUP_NAME_EN = sheet.getCell(7, i);
            Cell SGROUP_NAME_FRENCH = sheet.getCell(8, i);
            Cell AUTHORITY = sheet.getCell(9, i);
            Cell DEVICE_TYPE = sheet.getCell(10, i);

            if ("".equals(PN.getContents())) {//如果读取的数据为空
                break;
            }

            if (!only.containsKey(PN.getContents() + "-" + GROUP.getContents())) {
                groupInfos.add(new GroupInfo(PN.getContents(),
                        GROUP.getContents(),
                        GROUP_NAME_CN.getContents(),
                        GROUP_NAME_EN.getContents(),
                        GROUP_NAME_FRENCH.getContents(),
                        Integer.valueOf(DEVICE_TYPE.getContents())));

                only.put(PN.getContents() + "-" + GROUP.getContents(), "");
            }

            sGroupInfos.add(new SGroupInfo(
                    PN.getContents(),
                    GROUP.getContents(),
                    GROUP_NAME_CN.getContents(),
                    GROUP_NAME_EN.getContents(),
                    GROUP_NAME_FRENCH.getContents(),
                    SGROUP.getContents(),
                    SGROUP_NAME_CN.getContents(),
                    SGROUP_NAME_EN.getContents(),
                    SGROUP_NAME_FRENCH.getContents(),
                    AUTHORITY.getContents()));

        }
        ObjectBox.get().getBoxStore().boxFor(SGroupInfo.class).put(sGroupInfos);
        ObjectBox.get().getBoxStore().boxFor(GroupInfo.class).put(groupInfos);
        workbook.close();
    }

    private void setupRecord() throws IOException, BiffException {
        Workbook workbook = Workbook.getWorkbook(Fastgo.getContext().getAssets().open("RECORD_POINT.xls"));
        //获得第一个工作表对象(ecxel中sheet的编号从0开始,0,1,2,3,....)
        Sheet sheet = workbook.getSheet(0);

        List<RecordPoint> rows = new ArrayList<>();
        for (int i = 1; i < sheet.getRows(); i++) {
            //获取每一行的单元格
            Cell templateCell = sheet.getCell(0, i);//（列，行）
            Cell templateNameCell = sheet.getCell(1, i);//（列，行）
            Cell codeCell = sheet.getCell(2, i);//（列，行）
            Cell meansCNCell = sheet.getCell(3, i);
            Cell v0Cell = sheet.getCell(4, i);
            Cell v1Cell = sheet.getCell(5, i);
            Cell signCell = sheet.getCell(6, i);
            Cell unitCell = sheet.getCell(7, i);
            Cell accuracyCell = sheet.getCell(8, i);
            Cell v2Cell = sheet.getCell(9, i);
            Cell v3Cell = sheet.getCell(10, i);

            if ("".equals(templateCell.getContents().trim())) {//如果读取的数据为空
                break;
            }

            rows.add(new RecordPoint(
                    Integer.valueOf(templateCell.getContents().trim()),
                    templateNameCell.getContents().trim(),
                    Integer.valueOf(codeCell.getContents().trim()),
                    meansCNCell.getContents().trim(),
                    v0Cell.getContents().trim(),
                    v1Cell.getContents().trim(),
                    StringUtils.isEmpty(signCell.getContents()) ? 0 : Integer.valueOf(signCell.getContents().trim()),
                    unitCell.getContents(),
                    StringUtils.isEmpty(accuracyCell.getContents()) ? 0 : Integer.valueOf(accuracyCell.getContents().trim()),
                    v2Cell.getContents().trim(),
                    v3Cell.getContents().trim()));
        }
        ObjectBox.get().getBoxStore().boxFor(RecordPoint.class).put(rows);
        workbook.close();
    }

    /**
     * 数据分组（大组）
     *
     * @param pn
     * @return
     */
    public List<GroupInfo> getGroupsWith(int pn) {
        QueryBuilder<GroupInfo> builder = ObjectBox.get().getBoxStore().boxFor(GroupInfo.class).query()
                .equal(GroupInfo_.pn, String.valueOf(pn));

        if (LocalUserManager.getDeviceType() != 0x02 && LocalUserManager.getDeviceType() != 0x0B) {
            //光伏设备
            builder.and().equal(GroupInfo_.deviceType, Frame.光伏光储);
        }

        return builder.build().find();
    }

    /**
     * 获取数据点列表
     *
     * @param pn
     * @param group
     * @return
     */
    public List<PointInfo> getPointInfosWith(int pn, String group, String authority) {

        final String normalRoleName = "normal";
        final String opsRoleName = "ops";

        QueryBuilder<PointInfo> builder = ObjectBox.get().getBoxStore().boxFor(PointInfo.class).query()
                .equal(PointInfo_.pn, String.valueOf(pn))
                .and().equal(PointInfo_.group, group);

        if (LocalUserManager.getDeviceType() != 0x02 && LocalUserManager.getDeviceType() != 0x0B) {
            //光伏设备
            builder.and().equal(PointInfo_.deviceType, Frame.光伏光储);
        }

        if (normalRoleName.equals(authority)) {
            builder.and().equal(PointInfo_.authority, normalRoleName);
        } else if (opsRoleName.equals(authority)) {
            builder.equal(PointInfo_.authority, normalRoleName)
                    .or().equal(PointInfo_.authority, opsRoleName);
        }

        return builder.build().find();
    }

    /**
     * 查询本地模式告警数据
     *
     * @param authority 用户角色名
     * @return
     */
    public LocalAlarmList getLocalAlarms(String authority) {
        LocalAlarmList localAlarmListList = new LocalAlarmList();

        final int type = 1;//1为告警
        final String parseValue = "1";//1为异常
        final String CommonAlarmValue = "告警";
        final String SeriousAlarmValue = "故障";

        final String normalRoleName = "normal";
        final String opsRoleName = "ops";
        final String factoryName = "factory";

        long commonCount = 0;
        long seriousCount = 0;

        Query<DeviceData> query = ObjectBox.get().getBoxStore().boxFor(DeviceData.class).query()
                .equal(DeviceData_.type, type)
                .equal(DeviceData_.parseValue, parseValue)
                .equal(DeviceData_.authority, "")
                .equal(DeviceData_.sgroup, CommonAlarmValue)//目前只有故障和告警两类
                .or().equal(DeviceData_.sgroup, SeriousAlarmValue)
                .build();

        Query<DeviceData> queryCount = ObjectBox.get().getBoxStore().boxFor(DeviceData.class).query()
                .equal(DeviceData_.type, type)
                .equal(DeviceData_.parseValue, parseValue)
                .equal(DeviceData_.authority, "")
                .equal(DeviceData_.sgroup, CommonAlarmValue)//目前只有故障和告警两类
                .build();

        //普通用户的告警，所有用户都能查看，所以都放进去
        List<DeviceData> deviceDatas = query.setParameter(DeviceData_.authority, normalRoleName).find();
        deviceDatas = deviceDatas != null && deviceDatas.size() > 0 ?
                deviceDatas : new ArrayList<DeviceData>();

        localAlarmListList.setDeviceDataList(deviceDatas);

        commonCount = queryCount.setParameter(DeviceData_.authority, normalRoleName)
                .setParameter(DeviceData_.sgroup, CommonAlarmValue).count();
        seriousCount = queryCount.setParameter(DeviceData_.authority, normalRoleName)
                .setParameter(DeviceData_.sgroup, SeriousAlarmValue).count();

        //如果是运维人员角色或者是厂家角色，则添加运维人员才能看的告警
        if (opsRoleName.equals(authority) || factoryName.equals(authority)) {
            List<DeviceData> opsDeviceDatas = query.setParameter(DeviceData_.authority, opsRoleName).find();
            opsDeviceDatas = opsDeviceDatas != null && opsDeviceDatas.size() > 0 ?
                    opsDeviceDatas : new ArrayList<DeviceData>();

            localAlarmListList.getDeviceDataList().addAll(opsDeviceDatas);

            commonCount += queryCount.setParameter(DeviceData_.authority, opsRoleName)
                    .setParameter(DeviceData_.sgroup, CommonAlarmValue).count();
            seriousCount += queryCount.setParameter(DeviceData_.authority, opsRoleName)
                    .setParameter(DeviceData_.sgroup, SeriousAlarmValue).count();
        }
        //如果是厂家角色，则添加厂家才能看的告警
        if (factoryName.equals(authority)) {
            List<DeviceData> factoryDeviceDatas = query.setParameter(DeviceData_.authority, factoryName).find();
            factoryDeviceDatas = factoryDeviceDatas != null && factoryDeviceDatas.size() > 0 ?
                    factoryDeviceDatas : new ArrayList<DeviceData>();

            localAlarmListList.getDeviceDataList().addAll(factoryDeviceDatas);

            commonCount += queryCount.setParameter(DeviceData_.authority, factoryName)
                    .setParameter(DeviceData_.sgroup, CommonAlarmValue).count();
            seriousCount += queryCount.setParameter(DeviceData_.authority, factoryName)
                    .setParameter(DeviceData_.sgroup, SeriousAlarmValue).count();
        }

        localAlarmListList.setCommonAlarmCount(commonCount);
        localAlarmListList.setSeriousAlarmCount(seriousCount);
        return localAlarmListList;
    }

    public void saveCollectorKey(String key){
        String keysStr = SPUtils.getInstance().getString("COLLECTOR_KEYS","");
        if(!keysStr.contains(key)){
            keysStr = keysStr+"&&"+key;
            SPUtils.getInstance().put("COLLECTOR_KEYS",keysStr);
        }
    }

    public boolean hasCollectorKey(String key){
        String keysStr = SPUtils.getInstance().getString("COLLECTOR_KEYS","");
        return keysStr.contains(key);
    }

    public void saveLanguageSelect(String language){
        SPUtils.getInstance().put("LANGUAGE",language);
    }

    public String getLanguageSelect(){
        return SPUtils.getInstance().getString("LANGUAGE","");
    }

}
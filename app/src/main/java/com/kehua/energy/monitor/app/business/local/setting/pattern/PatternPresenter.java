package com.kehua.energy.monitor.app.business.local.setting.pattern;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.model.entity.ModbusResponse;
import com.kehua.energy.monitor.app.model.entity.PatternEntity;
import com.kehua.energy.monitor.app.model.entity.PatternHead;
import com.kehua.energy.monitor.app.model.entity.PointInfo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.scope.FragmentScope;

@FragmentScope
public class PatternPresenter extends PatternContract.Presenter {

    PatternContract.View mView;

    @Inject
    APPModel mModel;

    List<MultiItemEntity> data;

    @Inject
    public PatternPresenter() {
        data = new ArrayList<>();
    }

    @Override
    public void attachView(PatternContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    public void setupData() {

        List<PointInfo> oriPointInfos = mModel.getLocalModel().getPointInfosWith(LocalUserManager.getPn(), Frame.模式设置, LocalUserManager.getRoleAuthority());
        List<PointInfo> pointInfos = new ArrayList<>();

        //如果是光储，则展示全部，否则只展示光伏（1）
        if (Frame.isStorageDevice(LocalUserManager.getDeviceType())) {
            pointInfos = oriPointInfos;
        } else {
            for (PointInfo pointInfo : oriPointInfos) {
                if (pointInfo.getDeviceType() == 1) {
                    pointInfos.add(pointInfo);
                }
            }
        }

        dealPointInfoList(pointInfos);
        mView.onSetupData(data);

        //启动采集
        if (LocalUserManager.getPn() == Frame.单相协议) {
            mModel.getRemoteModel().patternSettingInfoSinglePhaseProtocol(LocalUserManager.getDeviceAddress(), new Consumer<ModbusResponse>() {
                @Override
                public void accept(ModbusResponse modbusResponse) throws Exception {
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Logger.e(throwable.getMessage());
                }
            });
        } else {
            mModel.getRemoteModel().patternSettingInfoThreePhaseProtocol(LocalUserManager.getDeviceAddress(), new Consumer<ModbusResponse>() {
                @Override
                public void accept(ModbusResponse modbusResponse) throws Exception {
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Logger.e(throwable.getMessage());
                }
            });
        }
    }

    private void dealPointInfoList(List<PointInfo> pointInfos) {

        if (pointInfos == null || pointInfos.size() < 1) {
            return;
        }
        data.clear();

        //先获取标题行数据
        for (PointInfo pointInfo : pointInfos) {
            //如果Sgroup属性空白，则是标题数据
            if (StringUtils.isTrimEmpty(pointInfo.getSgroup())) {
                //除了"L/HVRT模式"与"L/HFRT模式" 为文字列表，其他的都是折线图
                if (pointInfo.getAddress().equals(String.valueOf(Frame.L_HVRT模式地址))
                        || pointInfo.getAddress().equals(String.valueOf(Frame.L_HFRT模式地址))) {
                    PatternHead patternHead = new PatternHead(pointInfo, false);
                    //L_HFRT模式地址点表里值是int类型，但是原型上是开关类型，所以需要强制指定
                    patternHead.setItemType(pointInfo.getAddress().equals(String.valueOf(Frame.L_HFRT模式地址)) ? PatternHead.HEAD_SWITCH : PatternHead.HEAD_TEXT);
                    data.add(patternHead);
                } else {
                    PatternHead patternHead = new PatternHead(pointInfo, true);
                    //Q-V模式 和 SPF模式 地址点表里值是int类型，但是原型上是开关类型，所以需要强制指定
                    boolean isSpecialModel = pointInfo.getAddress().equals(String.valueOf(Frame.Q_V模式地址))
                            || pointInfo.getAddress().equals(String.valueOf(Frame.SPF模式));
                    patternHead.setItemType(isSpecialModel ? PatternHead.HEAD_SWITCH : PatternHead.HEAD_TEXT);
                    data.add(patternHead);
                }
            }
        }
        //将内容数据依次归类
        for (PointInfo pointInfo : pointInfos) {
            //如果Sgroup属性不是空白，则是内容数据
            if (!StringUtils.isTrimEmpty(pointInfo.getSgroup())) {
                //归类
                for (MultiItemEntity multiItemEntity : data) {
                    PatternHead patternHead = (PatternHead) multiItemEntity;
                    if (pointInfo.getSgroup().equals(patternHead.getPointInfo().getDescriptionCN())) {
                        patternHead.addSubItemData(pointInfo);
                    }
                }
            }
        }
        //将数据进行处理
        for (MultiItemEntity multiItemEntity : data) {
            PatternHead patternHead = (PatternHead) multiItemEntity;
            if (patternHead.getSubItemData() == null && patternHead.getSubItemData().size() < 1) {
                continue;
            }
            //数据处理(折线图的特殊处理，文字的就直接田间)
            if (patternHead.isForLineChart()) {
                List<PointInfo> dealPointInfos = patternHead.getSubItemData();
                PatternEntity patternEntity = dealLineForSingleOrMul(patternHead, dealPointInfos);
                if (patternEntity != null) {
                    patternHead.addSubItem(patternEntity);
                }
                //QV模式还有一个尾巴 Q-V模式Hysteresis,用文字展示
                if (patternHead.getPointInfo().getAddress().equals(String.valueOf(Frame.Q_V模式地址))
                        && dealPointInfos != null && dealPointInfos.size() > 0) {
                    patternHead.addSubItem(new PatternEntity(dealPointInfos.get(dealPointInfos.size() - 1)));
                }
            } else {
                for (PointInfo pointInfo : patternHead.getSubItemData()) {
                    patternHead.addSubItem(new PatternEntity(pointInfo));
                }
            }
            //添加空白
            patternHead.addSubItem(new PatternEntity());
        }
    }

    private PatternEntity dealLineForSingleOrMul(PatternHead patternHead, List<PointInfo> dealPointInfos) {
        //因为图表对应的x,y均存放在里面，所以至少2个才是一组
        if (dealPointInfos == null || dealPointInfos.size() < 2) {
            return null;
        }
        PatternEntity patternEntity = null;
        boolean isPVModle = false;
        boolean isPFModle = false;

        String[] lables = new String[2];
        int[] colors = new int[]{R.color.red, R.color.green};
        if (patternHead.getPointInfo().getAddress().equals(String.valueOf(Frame.P_V模式模式地址))) {
            isPVModle = true;
            lables[0] = "P-V模式放电";
            lables[1] = "P-V模式充电";
        } else if (patternHead.getPointInfo().getAddress().equals(String.valueOf(Frame.P_F模式模式地址))) {
            isPFModle = true;
            lables[0] = "P-F模式放电";
            lables[1] = "P-F模式充电";
        }

        //P-V与P-F是双折线，其余单折线
        if (!isPVModle && !isPFModle) {
            patternEntity = new PatternEntity(dealPointInfos);
        } else {
            List<PointInfo> pointInfoList1 = new ArrayList<>();
            List<PointInfo> pointInfoList2 = new ArrayList<>();
            long adress;
            for (int i = 0; i < dealPointInfos.size(); i++) {
                adress = Long.parseLong(dealPointInfos.get(i).getAddress());
                //因为点表中放电地址集合在充电地址集合之前
                if ((isPVModle && adress < Frame.P_V模式充电起始地址) || (isPFModle && adress < Frame.P_F模式充电起始地址)) {
                    pointInfoList1.add(dealPointInfos.get(i));
                } else {
                    pointInfoList2.add(dealPointInfos.get(i));
                }
            }
            List<List<PointInfo>> listArray = new ArrayList<>();
            if (pointInfoList1.size() > 1) {
                listArray.add(pointInfoList1);
            }
            if (pointInfoList2.size() > 1) {
                listArray.add(pointInfoList2);
            }

            if (listArray.size() > 0) {
                List<PointInfo>[] array = new ArrayList[listArray.size()];
                listArray.toArray(array);
                patternEntity = new PatternEntity(lables, colors, array);
            }

        }
        return patternEntity;
    }

    @Override
    public void expandList(PatternAdapter patternAdapter) {
        if (data != null && data.size() > 0) {
            PatternHead firstPatternHead = (PatternHead) data.get(0);
            int adress = Integer.valueOf(firstPatternHead.getPointInfo().getAddress().trim());
            DeviceData firstDevData = CacheManager.getInstance().get(adress);
            if (firstDevData != null) {
                PatternHead patternHead;
                DeviceData deviceData;
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i) instanceof PatternHead) {
                        patternHead = (PatternHead) data.get(i);
                        deviceData = CacheManager.getInstance().get(Integer.valueOf(patternHead.getPointInfo().getAddress().trim()));
                        switch (patternHead.getItemType()) {
                            case PatternAdapter.TYPE_HEAD_TEXT:
                            case PatternAdapter.TYPE_HEAD_SWITCH:
                                if (deviceData.getIntValue() == Frame.OFF) {
                                    patternAdapter.collapse(i, true);
                                } else {
                                    patternAdapter.expand(i, true);
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

}
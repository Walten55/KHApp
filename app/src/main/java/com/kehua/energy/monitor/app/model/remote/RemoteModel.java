package com.kehua.energy.monitor.app.model.remote;

import android.util.ArrayMap;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.internal.LinkedTreeMap;
import com.hwangjr.rxbus.RxBus;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.entity.Cmd;
import com.kehua.energy.monitor.app.model.entity.Collector;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.model.entity.InvInfoList;
import com.kehua.energy.monitor.app.model.entity.ModbusResponse;
import com.kehua.energy.monitor.app.model.entity.RecordData;
import com.kehua.energy.monitor.app.model.entity.Upgrade;
import com.kehua.energy.monitor.app.model.local.LocalModel;
import com.kehua.energy.monitor.app.utils.ByteUtils;
import com.kehua.energy.monitor.app.utils.PasswordUtils;

import org.reactivestreams.Publisher;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.walten.fastgo.base.mvp.BaseModel;
import me.walten.fastgo.base.mvp.IModel;
import me.walten.fastgo.integration.IRepositoryManager;
import me.walten.fastgo.utils.XRxUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class RemoteModel extends BaseModel implements IModel {


    @Inject
    LocalModel localModel;

    @Inject
    public RemoteModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    /**
     * 联网设置
     *
     * @param ssid
     * @param pwd
     * @param subscriber
     * @param throwable
     */
    public void apset(String ssid, String pwd, Consumer<LinkedTreeMap<String, String>> subscriber, Consumer<Throwable> throwable) {
        HashMap<String, Object> req = new HashMap<>();
        req.put("ssid", ssid);
        req.put("pwd", pwd);

        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .apset(req)
                .compose(XRxUtil.<LinkedTreeMap<String, String>>getHttpDefaultScheduler())
                .subscribe(subscriber, throwable));

    }

    /**
     * 透传
     *
     * @param data
     * @param subscriber
     * @param throwable
     */
    public void fdbg(String data, Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        HashMap<String, Object> req = new HashMap<>();
        req.put("data", data);

        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .subscribe(subscriber, throwable));
    }

    /**
     * 透传
     *
     * @param data
     * @param subscriber
     * @param throwable
     */
    public void fdbgMainThread(String data, Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        HashMap<String, Object> req = new HashMap<>();
        req.put("data", data);

        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribe(subscriber, throwable));
    }

    /**
     * 获取采集器连接的设备
     *
     * @param subscriber
     * @param throwable
     */
    public void invinfo(Consumer<InvInfoList> subscriber, Consumer<Throwable> throwable) {
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .invinfo()
                .compose(XRxUtil.<InvInfoList>getHttpDefaultScheduler())
                .subscribe(subscriber, throwable));
    }

    /**
     * 升级状态
     *
     * @param subscriber
     * @param throwable
     */
    public void upgrade(Consumer<Upgrade> subscriber, Consumer<Throwable> throwable) {
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .upgrade()
                .compose(XRxUtil.<Upgrade>getHttpDefaultScheduler())
                .subscribe(subscriber, throwable));
    }

    /**
     * 采集器信息
     */
    public void getdev(Consumer<Collector> subscriber, Consumer<Throwable> throwable) {
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .getdev()
                .compose(XRxUtil.<Collector>getHttpDefaultScheduler())
                .subscribe(subscriber, throwable));
    }

    /**
     * 采集SN、设备类型
     *
     * @param subscriber
     * @param throwable
     */
    public void snAndDeviceType(final int deviceAddress, final Consumer<ArrayMap<String, Object>> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.协议类型采集[0], Frame.协议类型采集[1]));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            int type = ByteUtils.bytes2Int(modbusResponse.getBytesDat());

                            if (type == Frame.单相协议) {
                                req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.单相串号采集[0], Frame.单相串号采集[1]));
                            } else if (type == Frame.三相协议) {
                                req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.三相串号采集[0], Frame.三相串号采集[1]));
                            }
                            resultMap.put("pn", type);
                            return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                    .fdbg(req);
                        }

                        return null;
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            resultMap.put("sn", ByteUtils.bytes2String(modbusResponse.getBytesDat()));
                            req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.设备类型采集[0], Frame.设备类型采集[1]));
                            return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                    .fdbg(req);
                        }

                        return null;
                    }
                })
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            resultMap.put("deviceType", ByteUtils.bytes2Int(modbusResponse.getBytesDat()));
                        }

                        if (subscriber != null) {
                            subscriber.accept(resultMap);
                        }
                    }
                }, throwable));
    }

    /**
     * 单相协议基本设置信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void basicSettingInfoSinglePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x01, Frame.开关机地址, Frame.开关机地址 + 7));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), Frame.开关机地址, Frame.开关机地址 + 7, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, Frame.工作模式地址, Frame.工作模式地址));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), Frame.工作模式地址, Frame.工作模式地址, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, Frame.充放电时段采集[0], Frame.充放电时段采集[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), Frame.充放电时段采集[0], Frame.充放电时段采集[1], Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }

    /**
     * 三相协议基本设置信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void basicSettingInfoThreePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x01, Frame.开关机地址, Frame.开关机地址 + 7));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), Frame.开关机地址, Frame.开关机地址 + 7, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, Frame.充放电时段采集[0], Frame.充放电时段采集[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), Frame.充放电时段采集[0], Frame.充放电时段采集[1], Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }

    /**
     * 单相协议本地模式设置信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void patternSettingInfoSinglePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, Frame.本地模式设置_模拟量_采集[0], Frame.本地模式设置_模拟量_采集[1]));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), Frame.本地模式设置_模拟量_采集[0], Frame.本地模式设置_模拟量_采集[1], Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }

    /**
     * 三相协议模式设置信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void patternSettingInfoThreePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, Frame.本地模式设置_模拟量_采集[0], Frame.本地模式设置_模拟量_采集[1]));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), Frame.本地模式设置_模拟量_采集[0], Frame.本地模式设置_模拟量_采集[1], Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }

    /**
     * 单相协议高级设置信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void advancedSettingInfoSinglePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x01, 5001, 5016));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 5001, 5016, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x01, 5066, 5077));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 5066, 5077, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x01, 5176, 5177));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 5176, 5177, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6000, 6011));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6000, 6011, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6201, 6203));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6201, 6203, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6300, 6322));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6300, 6322, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6422, 6422));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6422, 6422, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6650, 6651));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6650, 6651, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6602, 6604));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6602, 6604, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }

    /**
     * 单相协议高级设置状态量信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void advancedStatusSettingInfoSinglePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x01, 5001, 5016));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 5001, 5016, Frame.单相协议, new Date());
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x01, 5066, 5077));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 5066, 5077, Frame.单相协议, new Date());
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x01, 5176, 5177));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 5176, 5177, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }
                        if (subscriber != null)
                            subscriber.accept(modbusResponse);

                    }
                }, throwable));
    }

    /**
     * 三相协议高级设置信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void advancedSettingInfoThreePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x01, 5001, 5009));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 5001, 5009, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x01, 5064, 5096));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 5064, 5096, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6000, 6005));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6000, 6005, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6201, 6202));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6201, 6202, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6300, 6310));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6300, 6310, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6422, 6422));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6422, 6422, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6602, 6604));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6602, 6604, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }


    /**
     * 三相相协议高级设置状态量信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void advancedStatusSettingInfoThreePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x01, 5001, 5096));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 5001, 5096, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                        }
                        if (subscriber != null)
                            subscriber.accept(modbusResponse);

                    }
                }, throwable));
    }

    /**
     * 单相协议电网设置信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void gridSettingInfoSinglePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x01, 5001, 5016));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 5001, 5016, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6302, 6302));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6302, 6302, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6400, 6487));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6400, 6487, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }

    /**
     * 三相协议电网设置信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void gridSettingInfoThreePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6200, 6200));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6200, 6200, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6400, 6421));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6400, 6421, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }

    /**
     * 单相协议电池设置信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void batterySettingInfoSinglePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x01, 5001, 5016));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 5001, 5016, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6006, 6010));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6006, 6010, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6204, 6204));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6204, 6204, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6308, 6318));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6308, 6318, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }

    /**
     * 三相协议电池设置信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void batterySettingInfoThreePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6203, 6206));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6203, 6206, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6350, 6360));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6350, 6360, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }

    /**
     * 单相协议校准设置信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void calibrationSettingInfoSinglePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6660, 6677));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6660, 6677, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }

    /**
     * 三相协议校准设置信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void calibrationSettingInfoThreePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6660, 7740));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6660, 7740, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }

    /**
     * 单相协议设备设置信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void deviceSettingInfoSinglePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6800, 6825));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6800, 6825, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 7000, 7010));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 7000, 7010, Frame.单相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }

    /**
     * 三相协议设备设置信息采集
     *
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void deviceSettingInfoThreePhaseProtocol(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 6800, 6825));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 6800, 6825, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x03, 7000, 7010));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 7000, 7010, Frame.三相协议, new Date());
                            RxBus.get().post(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE, "");

                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }

    /**
     * 单相协议采集
     *
     * @param deviceAddress
     */
    public void collectingSinglePhaseProtocol(final int deviceAddress) {
        final ArrayMap<String, String> sn = new ArrayMap<>();
        sn.put("sn", "no_sn");

        //采集时间做一个一致性优化
        final Date updateTime = new Date();

        //状态量采集帧集合 2帧合成 再存储
        final List<DeviceData> frame = new ArrayList<>();

        //采集 为了避免权限错误先得到串号 再下发权限密码 最后采集
        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.单相串号采集[0], Frame.单相串号采集[1]));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            long password = 333;

                            if(!StringUtils.isEmpty(ByteUtils.bytes2String(modbusResponse.getBytesDat()))){
                                sn.put("sn", ByteUtils.bytes2String(modbusResponse.getBytesDat()));
                                password = PasswordUtils.createPassword(31, ByteUtils.bytes2String(modbusResponse.getBytesDat()));
                            }

                            //授厂家权限
                            req.put("data", Cmd.newWriteCmd(deviceAddress, 6600, 6601, (int) password));
                            return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                    .fdbg(req);
                        }

                        return null;
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //=======以下为状态量采集=======
                            req.put("data", Cmd.newReadCmd(deviceAddress, 0x02, Frame.单相_状态量_采集1[0], Frame.单相_状态量_采集1[1]));
                            return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                    .fdbg(req);
                        }

                        return null;
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.单相_状态量_采集1[0], Frame.单相_状态量_采集1[1], Frame.单相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x02, Frame.单相_状态量_采集2[0], Frame.单相_状态量_采集2[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.单相_状态量_采集2[0], Frame.单相_状态量_采集2[1], Frame.单相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        //=======以下为模拟量采集=======
                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.单相_模拟量_采集1[0], Frame.单相_模拟量_采集1[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.单相_模拟量_采集1[0], Frame.单相_模拟量_采集1[1], Frame.单相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.单相_模拟量_采集2[0], Frame.单相_模拟量_采集2[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.单相_模拟量_采集2[0], Frame.单相_模拟量_采集2[1], Frame.单相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.单相_模拟量_采集3[0], Frame.单相_模拟量_采集3[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.单相_模拟量_采集3[0], Frame.单相_模拟量_采集3[1], Frame.单相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.单相_模拟量_采集4[0], Frame.单相_模拟量_采集4[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.单相_模拟量_采集4[0], Frame.单相_模拟量_采集4[1], Frame.单相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.单相_模拟量_采集5[0], Frame.单相_模拟量_采集5[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);

                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.单相_模拟量_采集5[0], Frame.单相_模拟量_采集5[1], Frame.单相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.单相_模拟量_采集6[0], Frame.单相_模拟量_采集6[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.单相_模拟量_采集6[0], Frame.单相_模拟量_采集6[1], Frame.单相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.单相_模拟量_采集7[0], Frame.单相_模拟量_采集7[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.单相_模拟量_采集7[0], Frame.单相_模拟量_采集7[1], Frame.单相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        //删除所有状态量
                        localModel.removeDeviceDataWithType(DeviceData.TYPE_STATUS);
                        //删除所有采集量
                        localModel.removeDeviceDataWithType(DeviceData.TYPE_HOLD);
                        //保存
                        localModel.saveDeviceDataList(frame);

                        //发送采集完成通知
                        RxBus.get().post(Config.EVENT_CODE_COLLECT_COMPLETE, "");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //删除所有状态量
                        localModel.removeDeviceDataWithType(DeviceData.TYPE_STATUS);
                        //删除所有采集量
                        localModel.removeDeviceDataWithType(DeviceData.TYPE_HOLD);
                        //保存
                        localModel.saveDeviceDataList(frame);
                    }
                }));


    }


    /**
     * 三相协议采集
     *
     * @param deviceAddress
     */
    public void collectingThreePhaseProtocol(final int deviceAddress) {
        final ArrayMap<String, String> sn = new ArrayMap<>();
        sn.put("sn", "no_sn");

        //采集时间做一个一致性优化
        final Date updateTime = new Date();

        //状态量采集帧集合 2帧合成 再存储
        final List<DeviceData> frame = new ArrayList<>();

        //采集 为了避免权限错误先得到串号 再下发权限密码 最后采集
        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.三相串号采集[0], Frame.三相串号采集[1]));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {

                        if (modbusResponse.isSuccess()) {
                            long password = 333;

                            if(!StringUtils.isEmpty(ByteUtils.bytes2String(modbusResponse.getBytesDat()))){
                                sn.put("sn", ByteUtils.bytes2String(modbusResponse.getBytesDat()));
                                password = PasswordUtils.createPassword(31, ByteUtils.bytes2String(modbusResponse.getBytesDat()));
                            }

                            //授厂家权限
                            req.put("data", Cmd.newWriteCmd(deviceAddress, 6600, 6601, (int) password));
                            return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                    .fdbg(req);
                        }

                        return null;
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //=======以下为状态量采集=======
                            req.put("data", Cmd.newReadCmd(deviceAddress, 0x02, Frame.三相_状态量_采集1[0], Frame.三相_状态量_采集1[1]));
                            return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                    .fdbg(req);
                        }

                        return null;
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.三相_状态量_采集1[0], Frame.三相_状态量_采集1[1], Frame.三相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x02, Frame.三相_状态量_采集2[0], Frame.三相_状态量_采集2[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.三相_状态量_采集2[0], Frame.三相_状态量_采集2[1], Frame.三相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        //=======以下为模拟量采集=======
                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x02, Frame.三相_状态量_采集3[0], Frame.三相_状态量_采集3[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.三相_状态量_采集3[0], Frame.三相_状态量_采集3[1], Frame.三相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        //=======以下为模拟量采集=======
                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.三相_模拟量_采集1[0], Frame.三相_模拟量_采集1[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.三相_模拟量_采集1[0], Frame.三相_模拟量_采集1[1], Frame.三相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.三相_模拟量_采集2[0], Frame.三相_模拟量_采集2[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.三相_模拟量_采集2[0], Frame.三相_模拟量_采集2[1], Frame.三相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.三相_模拟量_采集3[0], Frame.三相_模拟量_采集3[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.三相_模拟量_采集3[0], Frame.三相_模拟量_采集3[1], Frame.三相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.三相_模拟量_采集4[0], Frame.三相_模拟量_采集4[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.三相_模拟量_采集4[0], Frame.三相_模拟量_采集4[1], Frame.三相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.三相_模拟量_采集5[0], Frame.三相_模拟量_采集5[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);

                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.三相_模拟量_采集5[0], Frame.三相_模拟量_采集5[1], Frame.三相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.三相_模拟量_采集6[0], Frame.三相_模拟量_采集6[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .flatMap(new Function<ModbusResponse, Publisher<ModbusResponse>>() {
                    @Override
                    public Publisher<ModbusResponse> apply(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.三相_模拟量_采集6[0], Frame.三相_模拟量_采集6[1], Frame.三相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, Frame.三相_模拟量_采集7[0], Frame.三相_模拟量_采集7[1]));
                        return mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                                .fdbg(req);
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse(sn.get("sn"), modbusResponse.getBytesDat(), Frame.三相_模拟量_采集7[0], Frame.三相_模拟量_采集7[1], Frame.三相协议, updateTime);
                            frame.addAll(frame.size(), data);
                        }

                        //删除所有状态量
                        localModel.removeDeviceDataWithType(DeviceData.TYPE_STATUS);
                        //删除所有采集量
                        localModel.removeDeviceDataWithType(DeviceData.TYPE_HOLD);
                        //保存
                        localModel.saveDeviceDataList(frame);

                        //发送采集完成通知
                        RxBus.get().post(Config.EVENT_CODE_COLLECT_COMPLETE, "");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //删除所有状态量
                        localModel.removeDeviceDataWithType(DeviceData.TYPE_STATUS);
                        //删除所有采集量
                        localModel.removeDeviceDataWithType(DeviceData.TYPE_HOLD);
                        //保存
                        localModel.saveDeviceDataList(frame);
                    }
                }));


    }


    /**
     * 设置密码
     */
    public void setPassword(int password,final Consumer<Boolean> consumer) {
        final HashMap<String, Object> req = new HashMap<>();
        //授厂家权限
        req.put("data", Cmd.newWriteCmd(LocalUserManager.getDeviceAddress(), 6600, 6601,password ));

        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
//                        if (modbusResponse.isSuccess()) {
//                            consumer.accept(true);
//                        }else {
//                            consumer.accept(false);
//                        }
                        consumer.accept(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        consumer.accept(false);
                    }
                }));
    }

    /**
     * 历史记录条数
     * @param deviceAddress
     * @param subscriber
     * @param throwable
     */
    public void recordCount(final int deviceAddress, final Consumer<ModbusResponse> subscriber, Consumer<Throwable> throwable) {
        final ArrayMap<String, Object> resultMap = new ArrayMap<>();

        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress, 0x04, 5700,5703));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            //解析
                            List<DeviceData> data = localModel.parse("", modbusResponse.getBytesDat(), 5700, 5703, Frame.通用协议, new Date());
                            if (subscriber != null)
                                subscriber.accept(modbusResponse);
                        }

                    }
                }, throwable));
    }

    /**
     * 历史记录配置
     */
    public void recordConfig(final int deviceAddress, int recordType,int index,int size,final Consumer<Boolean> consumer) {
        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newRecordConfig(deviceAddress,recordType,index,size));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io());
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            consumer.accept(true);
                        }else {
                            consumer.accept(false);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        consumer.accept(false);
                    }
                }));
    }

    /**
     * 获取记录
     */
    public void records(final int deviceAddress,final int template,final int index,final Consumer<List<RecordData>> consumer) {
        final HashMap<String, Object> req = new HashMap<>();
        req.put("data", Cmd.newReadCmd(deviceAddress,0x04,5710,5772));
        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .fdbg(req)
                .compose(new FlowableTransformer<ModbusResponse, ModbusResponse>() {
                    @Override
                    public Publisher<ModbusResponse> apply(Flowable<ModbusResponse> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribe(new Consumer<ModbusResponse>() {
                    @Override
                    public void accept(ModbusResponse modbusResponse) throws Exception {
                        if (modbusResponse.isSuccess()) {
                            List<DeviceData> deviceData = localModel.parse("", modbusResponse.getBytesDat(), 5710, 5712, Frame.通用协议, new Date(),false);
                            List<RecordData> recordData = localModel.parse(deviceAddress,template,ByteUtils.subArray(modbusResponse.getBytesDat(),6));

                            if(deviceData!=null&&deviceData.size()==3){
                                if(index == deviceData.get(1).getIntValue()){
                                    //与下发配置一致为有效数据
                                    if(consumer!=null){
                                        //移除前三条数据
                                        deviceData.remove(0);
                                        deviceData.remove(0);
                                        deviceData.remove(0);

                                        //倒序
                                        Collections.reverse(recordData);
                                        consumer.accept(recordData);
                                    }
                                }
                            }else {
                                //失败
                                if(consumer!=null){
                                    consumer.accept(null);
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(consumer!=null){
                            consumer.accept(null);
                        }
                    }
                }));
    }

    /**
     * 上传
     */
    public void upload(String filePath, final Consumer<Boolean> consumer) {
        File file = new File(filePath);
        // 创建 RequestBody，用于封装 请求RequestBody
        final RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        addSubscribe(mRepositoryManager.obtainRetrofitService(CollectorAPIService.class)
                .upload(body)
                .compose(new FlowableTransformer<ResponseBody, ResponseBody>() {
                    @Override
                    public Publisher<ResponseBody> apply(Flowable<ResponseBody> upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        consumer.accept(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        com.orhanobut.logger.Logger.e(throwable.getMessage());

                        if("Software caused connection abort".equals(throwable.getMessage())){
                            consumer.accept(true);
                        }else
                            consumer.accept(false);
                    }
                }));
    }
}
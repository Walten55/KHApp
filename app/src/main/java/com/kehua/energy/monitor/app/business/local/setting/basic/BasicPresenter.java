package com.kehua.energy.monitor.app.business.local.setting.basic;

import android.content.Context;
import android.util.SparseArray;

import com.blankj.utilcode.util.ActivityUtils;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.Cmd;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.model.entity.ModbusResponse;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.scope.FragmentScope;
import me.walten.fastgo.utils.XToast;

@FragmentScope
public class BasicPresenter extends BasicContract.Presenter {

    BasicContract.View mView;

    private SparseArray<DeviceData> cache = new SparseArray<>();

    @Inject
    APPModel mModel;

    Context localContext = ActivityUtils.getTopActivity() == null
            ? Fastgo.getContext() : ActivityUtils.getTopActivity();

    @Inject
    public BasicPresenter() {
    }

    @Override
    public void attachView(BasicContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
        cache.clear();
    }

    @Override
    public void setupData() {
        if(LocalUserManager.getPn() == Frame.单相协议){
            mModel.getRemoteModel().basicSettingInfoSinglePhaseProtocol(LocalUserManager.getDeviceAddress(), new Consumer<ModbusResponse>() {
                @Override
                public void accept(ModbusResponse modbusResponse) throws Exception {
                }
            },new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Logger.e(throwable.getMessage());
                }
            });
        }else {
            mModel.getRemoteModel().basicSettingInfoThreePhaseProtocol(LocalUserManager.getDeviceAddress(), new Consumer<ModbusResponse>() {
                @Override
                public void accept(ModbusResponse modbusResponse) throws Exception {
                }
            },new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Logger.e(throwable.getMessage());
                }
            });
        }
    }

    @Override
    public void power(boolean open, final Consumer<Boolean> consumer) {
        mView.startWaiting(localContext.getString(R.string.设置中));
        mModel.getRemoteModel().fdbgMainThread(Cmd.newWriteCmd(LocalUserManager.getDeviceAddress(), Frame.开关机地址, open), new Consumer<ModbusResponse>() {
            @Override
            public void accept(ModbusResponse modbusResponse) throws Exception {
                mView.stopWaiting();
                if( modbusResponse.isSuccess()){
                    setupData();
                    if(consumer!=null){
                        consumer.accept(true);
                    }
                }else {
                    XToast.error(localContext.getString(R.string.设置失败));
                    if(consumer!=null){
                        consumer.accept(false);
                    }
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.stopWaiting();
                Logger.e(throwable.getMessage());
                XToast.error(localContext.getString(R.string.设置失败));
                if(consumer!=null){
                    consumer.accept(false);
                }
            }
        });
    }

    @Override
    public void setWorkPattern(int mode, final Consumer<Boolean> consumer) {
        mView.startWaiting(localContext.getString(R.string.设置中));
        mModel.getRemoteModel().fdbgMainThread(Cmd.newWriteCmd(LocalUserManager.getDeviceAddress(), Frame.工作模式地址, mode), new Consumer<ModbusResponse>() {
            @Override
            public void accept(ModbusResponse modbusResponse) throws Exception {
                mView.stopWaiting();
                if( modbusResponse.isSuccess()){
                    //setupData();
                    XToast.success(localContext.getString(R.string.设置成功));
                    if(consumer!=null){
                        consumer.accept(true);
                    }
                }else {
                    XToast.error(localContext.getString(R.string.设置失败));
                    if(consumer!=null){
                        consumer.accept(false);
                    }
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.stopWaiting();
                Logger.e(throwable.getMessage());
                XToast.error(localContext.getString(R.string.设置失败));
                if(consumer!=null){
                    consumer.accept(false);
                }
            }
        });
    }

    @Override
    public void setSystemTime(Date date, final Consumer<Boolean> consumer) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int year = cal.get(Calendar.YEAR) - 2000;//获取年份（只取后两位）

        int month=cal.get(Calendar.MONTH)+1;//获取月份

        int day=cal.get(Calendar.DATE);//获取日

        int hour=cal.get(Calendar.HOUR_OF_DAY);//小时

        int minute=cal.get(Calendar.MINUTE);//分

        int second=cal.get(Calendar.SECOND);//秒

        mView.startWaiting(localContext.getString(R.string.设置中));
        mModel.getRemoteModel().fdbgMainThread(Cmd.newWriteCmd(LocalUserManager.getDeviceAddress(),6020,6025,new int[]{
                year,month,day,hour,minute,second
        }), new Consumer<ModbusResponse>() {
            @Override
            public void accept(ModbusResponse modbusResponse) throws Exception {
                mView.stopWaiting();
                if( modbusResponse.isSuccess()){
                    XToast.success(localContext.getString(R.string.设置成功));
                    if(consumer!=null){
                        consumer.accept(true);
                    }
                }else {
                    XToast.error(localContext.getString(R.string.设置失败));
                    if(consumer!=null){
                        consumer.accept(false);
                    }
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.stopWaiting();
                Logger.e(throwable.getMessage());
                XToast.error(localContext.getString(R.string.设置失败));
                if(consumer!=null){
                    consumer.accept(false);
                }
            }
        });
    }

    @Override
    public void setTimeFrame(List<String> chargeTimeFrames, List<String> dischargeTimeFrames, final Consumer<Boolean> consumer) {

        List<Integer> chargeTimeFramesInt = new ArrayList<>();
        for(int i = 0;i<chargeTimeFrames.size();i++){
            String temp = chargeTimeFrames.get(i);
            String[] hourAndMinute = temp.split(":");

            chargeTimeFramesInt.add(Integer.valueOf(hourAndMinute[0]));
            chargeTimeFramesInt.add(Integer.valueOf(hourAndMinute[1]));
        }

        List<Integer> dischargeTimeFramesInt = new ArrayList<>();
        for(int i = 0;i<dischargeTimeFrames.size();i++){
            String temp = dischargeTimeFrames.get(i);
            String[] hourAndMinute = temp.split(":");

            dischargeTimeFramesInt.add(Integer.valueOf(hourAndMinute[0]));
            dischargeTimeFramesInt.add(Integer.valueOf(hourAndMinute[1]));
        }

        mView.startWaiting(localContext.getString(R.string.设置中));
        mModel.getRemoteModel().fdbgMainThread(Cmd.newTimeFrameCmd(LocalUserManager.getDeviceAddress(),chargeTimeFramesInt,dischargeTimeFramesInt), new Consumer<ModbusResponse>() {
            @Override
            public void accept(ModbusResponse modbusResponse) throws Exception {
                mView.stopWaiting();
                if( modbusResponse.isSuccess()){
                    setupData();
                    XToast.success(localContext.getString(R.string.设置成功));
                    if(consumer!=null){
                        consumer.accept(true);
                    }
                }else {
                    XToast.error(localContext.getString(R.string.设置失败));
                    if(consumer!=null){
                        consumer.accept(false);
                    }
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.stopWaiting();
                Logger.e(throwable.getMessage());
                XToast.error(localContext.getString(R.string.设置失败));
                if(consumer!=null){
                    consumer.accept(false);
                }
            }
        });
    }
}
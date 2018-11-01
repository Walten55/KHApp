package com.kehua.energy.monitor.app.business.local.setting.advanced;

import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.Cmd;
import com.kehua.energy.monitor.app.model.entity.ModbusResponse;
import com.kehua.energy.monitor.app.model.entity.PointInfo;
import com.kehua.energy.monitor.app.model.entity.SettingEntity;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.utils.XToast;

//@FragmentScope
public class AdvancedPresenter extends AdvancedContract.Presenter {

    AdvancedContract.View mView;

    private List<SettingEntity> mSettingEntitys = new ArrayList<>();

    @Inject
    APPModel mModel;

    @Inject
    public AdvancedPresenter() {
    }

    @Override
    public void attachView(AdvancedContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    public void setupData() {
        mSettingEntitys.clear();
        List<PointInfo> pointInfos = mModel.getLocalModel().getPointInfosWith(LocalUserManager.getPn(), Frame.高级设置, LocalUserManager.getRoleAuthority());
        for (PointInfo info : pointInfos){
            mSettingEntitys.add(new SettingEntity(info));
        }
        mView.onSetupData(mSettingEntitys);


        if(LocalUserManager.getPn() == Frame.单相协议){
            mModel.getRemoteModel().advancedSettingInfoSinglePhaseProtocol(LocalUserManager.getDeviceAddress(), new Consumer<ModbusResponse>() {
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
            mModel.getRemoteModel().advancedSettingInfoThreePhaseProtocol(LocalUserManager.getDeviceAddress(), new Consumer<ModbusResponse>() {
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
    public void toggle(final int address,final boolean open,final Consumer<Boolean> consumer) {
        mView.startWaiting(Fastgo.getContext().getString(R.string.设置中));
        mModel.getRemoteModel().fdbgMainThread(Cmd.newWriteCmd(LocalUserManager.getDeviceAddress(), address, open), new Consumer<ModbusResponse>() {
            @Override
            public void accept(ModbusResponse modbusResponse) throws Exception {
                if( modbusResponse.isSuccess()){
                    updateStatusData();
                    if(consumer!=null)
                        consumer.accept(true);
                }else {
                    mView.stopWaiting();
                    XToast.error(Fastgo.getContext().getString(R.string.设置失败));
                    if(consumer!=null)
                        consumer.accept(false);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.stopWaiting();
                Logger.e(throwable.getMessage());
                XToast.error(Fastgo.getContext().getString(R.string.设置失败));
                if(consumer!=null)
                    consumer.accept(false);
            }
        });
    }

    @Override
    public void updateStatusData() {
        if(LocalUserManager.getPn() == Frame.单相协议){
            mModel.getRemoteModel().advancedStatusSettingInfoSinglePhaseProtocol(LocalUserManager.getDeviceAddress(), new Consumer<ModbusResponse>() {
                @Override
                public void accept(ModbusResponse modbusResponse) throws Exception {
                    mView.stopWaiting();
                    XToast.success(Fastgo.getContext().getString(R.string.设置成功));
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    mView.stopWaiting();
                }
            });

        }else {
            mModel.getRemoteModel().advancedStatusSettingInfoThreePhaseProtocol(LocalUserManager.getDeviceAddress(), new Consumer<ModbusResponse>() {
                @Override
                public void accept(ModbusResponse modbusResponse) throws Exception {
                    mView.stopWaiting();
                    XToast.success(Fastgo.getContext().getString(R.string.设置成功));
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    mView.stopWaiting();
                }
            });
        }
    }

    @Override
    public void save(int address, int value, final Consumer<Boolean> consumer) {
        mView.startWaiting(Fastgo.getContext().getString(R.string.设置中));
        mModel.getRemoteModel().fdbgMainThread(Cmd.newWriteCmd(LocalUserManager.getDeviceAddress(), address, value), new Consumer<ModbusResponse>() {
            @Override
            public void accept(ModbusResponse modbusResponse) throws Exception {
                mView.stopWaiting();
                if( modbusResponse.isSuccess()){
                    XToast.success(Fastgo.getContext().getString(R.string.设置成功));
                    if(consumer!=null){
                        consumer.accept(true);
                    }
                }else {
                    XToast.error(Fastgo.getContext().getString(R.string.设置失败));
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
                XToast.error(Fastgo.getContext().getString(R.string.设置失败));
                if(consumer!=null){
                    consumer.accept(false);
                }
            }
        });
    }

    @Override
    public void save(int address, int end, int value,final Consumer<Boolean> consumer) {
        mView.startWaiting(Fastgo.getContext().getString(R.string.设置中));
        mModel.getRemoteModel().fdbgMainThread(Cmd.newWriteCmd(LocalUserManager.getDeviceAddress(), address,end, value), new Consumer<ModbusResponse>() {
            @Override
            public void accept(ModbusResponse modbusResponse) throws Exception {
                mView.stopWaiting();
                if( modbusResponse.isSuccess()){
                    XToast.success(Fastgo.getContext().getString(R.string.设置成功));
                    if(consumer!=null){
                        consumer.accept(true);
                    }
                }else {
                    XToast.error(Fastgo.getContext().getString(R.string.设置失败));
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
                XToast.error(Fastgo.getContext().getString(R.string.设置失败));
                if(consumer!=null){
                    consumer.accept(false);
                }
            }
        });
    }

    @Override
    public void save(int address, int end, int[] values, final Consumer<Boolean> consumer) {
        mView.startWaiting(Fastgo.getContext().getString(R.string.设置中));
        mModel.getRemoteModel().fdbgMainThread(Cmd.newPowerOnAndProbationPeriodPwdCmd(LocalUserManager.getDeviceAddress(), values[0],values[1]), new Consumer<ModbusResponse>() {
            @Override
            public void accept(ModbusResponse modbusResponse) throws Exception {
                mView.stopWaiting();
                if( modbusResponse.isSuccess()){
                    XToast.success(Fastgo.getContext().getString(R.string.设置成功));
                    if(consumer!=null){
                        consumer.accept(true);
                    }
                }else {
                    XToast.error(Fastgo.getContext().getString(R.string.设置失败));
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
                XToast.error(Fastgo.getContext().getString(R.string.设置失败));
                if(consumer!=null){
                    consumer.accept(false);
                }
            }
        });
    }

    @Override
    public void save(int address, int end, String value,final Consumer<Boolean> consumer) {
        mView.startWaiting(Fastgo.getContext().getString(R.string.设置中));
        mModel.getRemoteModel().fdbgMainThread(Cmd.newWriteCmd(LocalUserManager.getDeviceAddress(), address,end, value), new Consumer<ModbusResponse>() {
            @Override
            public void accept(ModbusResponse modbusResponse) throws Exception {
                mView.stopWaiting();
                if( modbusResponse.isSuccess()){
                    XToast.success(Fastgo.getContext().getString(R.string.设置成功));
                    if(consumer!=null){
                        consumer.accept(true);
                    }
                }else {
                    XToast.error(Fastgo.getContext().getString(R.string.设置失败));
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
                XToast.error(Fastgo.getContext().getString(R.string.设置失败));
                if(consumer!=null){
                    consumer.accept(false);
                }
            }
        });
    }

    @Override
    public void save(String ssn, int sno, final Consumer<Boolean> consumer) {
        mView.startWaiting(Fastgo.getContext().getString(R.string.设置中));
        mModel.getRemoteModel().fdbgMainThread(Cmd.newStationSNAndStationNoCmd(LocalUserManager.getDeviceAddress(), ssn,sno), new Consumer<ModbusResponse>() {
            @Override
            public void accept(ModbusResponse modbusResponse) throws Exception {
                mView.stopWaiting();
                if( modbusResponse.isSuccess()){
                    XToast.success(Fastgo.getContext().getString(R.string.设置成功));
                    if(consumer!=null){
                        consumer.accept(true);
                    }
                }else {
                    XToast.error(Fastgo.getContext().getString(R.string.设置失败));
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
                XToast.error(Fastgo.getContext().getString(R.string.设置失败));
                if(consumer!=null){
                    consumer.accept(false);
                }
            }
        });
    }

    @Override
    public void save(String sn, int powerOnF, int probationPeriodF, int day, final Consumer<Boolean> consumer) {
        mView.startWaiting(Fastgo.getContext().getString(R.string.设置中));
        mModel.getRemoteModel().fdbgMainThread(Cmd.newAboutSNCmd(LocalUserManager.getDeviceAddress(), sn,powerOnF, probationPeriodF,day), new Consumer<ModbusResponse>() {
            @Override
            public void accept(ModbusResponse modbusResponse) throws Exception {
                mView.stopWaiting();
//                if( modbusResponse.isSuccess()){
//                    XToast.success(Fastgo.getContext().getString(R.string.设置成功));
//                    if(consumer!=null){
//                        consumer.accept(true);
//                    }
//                }else {
//                    XToast.error(Fastgo.getContext().getString(R.string.设置失败));
//                    if(consumer!=null){
//                        consumer.accept(false);
//                    }
//                }
                XToast.success(Fastgo.getContext().getString(R.string.设置成功));
                if(consumer!=null){
                    consumer.accept(true);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.stopWaiting();
                Logger.e(throwable.getMessage());
                XToast.error(Fastgo.getContext().getString(R.string.设置失败));
                if(consumer!=null){
                    consumer.accept(false);
                }
            }
        });
    }
}
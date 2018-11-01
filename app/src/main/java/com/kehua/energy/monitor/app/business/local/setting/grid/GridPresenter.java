package com.kehua.energy.monitor.app.business.local.setting.grid;

import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.ModbusResponse;
import com.kehua.energy.monitor.app.model.entity.PointInfo;
import com.kehua.energy.monitor.app.model.entity.SettingEntity;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.scope.FragmentScope;

@FragmentScope
public class GridPresenter extends GridContract.Presenter {

    GridContract.View mView;

    @Inject
    APPModel mModel;

    private List<SettingEntity> mSettingEntitys = new ArrayList<>();

    @Inject
    public GridPresenter() {
    }

    @Override
    public void attachView(GridContract.View view) {
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
        List<PointInfo> pointInfos = mModel.getLocalModel().getPointInfosWith(LocalUserManager.getPn(), Frame.电网设置, LocalUserManager.getRoleAuthority());
        for (PointInfo info : pointInfos){
            mSettingEntitys.add(new SettingEntity(info));
        }
        mView.onSetupData(mSettingEntitys);

        if(LocalUserManager.getPn() == Frame.单相协议){
            mModel.getRemoteModel().gridSettingInfoSinglePhaseProtocol(LocalUserManager.getDeviceAddress(), new Consumer<ModbusResponse>() {
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
            mModel.getRemoteModel().gridSettingInfoThreePhaseProtocol(LocalUserManager.getDeviceAddress(), new Consumer<ModbusResponse>() {
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
}
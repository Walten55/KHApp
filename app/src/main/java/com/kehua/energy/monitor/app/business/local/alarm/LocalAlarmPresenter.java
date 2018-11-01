package com.kehua.energy.monitor.app.business.local.alarm;

import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.LocalAlarmList;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.scope.FragmentScope;

@FragmentScope
public class LocalAlarmPresenter extends LocalAlarmContract.Presenter {

    LocalAlarmContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public LocalAlarmPresenter() {
    }

    @Override
    public void attachView(LocalAlarmContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    void loadLocalAlarm() {
        //本地登陆必定成功才可进入该界面
        LocalAlarmList localAlarmList = mModel.getLocalModel().getLocalAlarms(LocalUserManager.getRoleAuthority());
        mView.showLocalAlarms(localAlarmList);
    }
}
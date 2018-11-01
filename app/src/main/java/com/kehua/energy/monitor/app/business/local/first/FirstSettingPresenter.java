package com.kehua.energy.monitor.app.business.local.first;

import com.kehua.energy.monitor.app.model.APPModel;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.scope.ActivityScope;

@ActivityScope
public class FirstSettingPresenter extends FirstSettingContract.Presenter {

    FirstSettingContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public FirstSettingPresenter() {
    }

    @Override
    public void attachView(FirstSettingContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    public void setPassword(int password, Consumer<Boolean> consumer) {
        mModel.getRemoteModel().setPassword(password,consumer);
    }
}
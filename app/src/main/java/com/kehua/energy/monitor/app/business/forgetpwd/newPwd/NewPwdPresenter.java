package com.kehua.energy.monitor.app.business.forgetpwd.newPwd;

import com.kehua.energy.monitor.app.model.APPModel;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.scope.ActivityScope;

public class NewPwdPresenter extends NewPwdContract.Presenter {

    NewPwdContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public NewPwdPresenter() {
    }

    @Override
    public void attachView(NewPwdContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }
}
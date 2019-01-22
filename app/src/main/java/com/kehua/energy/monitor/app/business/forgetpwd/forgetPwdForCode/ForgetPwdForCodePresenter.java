package com.kehua.energy.monitor.app.business.forgetpwd.forgetPwdForCode;

import com.kehua.energy.monitor.app.model.APPModel;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.scope.ActivityScope;

public class ForgetPwdForCodePresenter extends ForgetPwdForCodeContract.Presenter {

    ForgetPwdForCodeContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public ForgetPwdForCodePresenter() {
    }

    @Override
    public void attachView(ForgetPwdForCodeContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }
}
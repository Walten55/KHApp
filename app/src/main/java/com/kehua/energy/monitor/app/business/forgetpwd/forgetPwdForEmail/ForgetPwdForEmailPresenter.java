package com.kehua.energy.monitor.app.business.forgetpwd.forgetPwdForEmail;

import com.kehua.energy.monitor.app.model.APPModel;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.scope.ActivityScope;

public class ForgetPwdForEmailPresenter extends ForgetPwdForEmailContract.Presenter {

    ForgetPwdForEmailContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public ForgetPwdForEmailPresenter() {
    }

    @Override
    public void attachView(ForgetPwdForEmailContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }
}
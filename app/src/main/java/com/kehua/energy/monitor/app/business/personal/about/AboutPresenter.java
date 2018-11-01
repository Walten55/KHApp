package com.kehua.energy.monitor.app.business.personal.about;

import com.kehua.energy.monitor.app.model.APPModel;

import javax.inject.Inject;

import me.walten.fastgo.di.scope.ActivityScope;

@ActivityScope
public class AboutPresenter extends AboutContract.Presenter {

    AboutContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public AboutPresenter() {
    }

    @Override
    public void attachView(AboutContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }
}
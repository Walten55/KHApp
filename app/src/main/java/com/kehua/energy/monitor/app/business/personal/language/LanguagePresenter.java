package com.kehua.energy.monitor.app.business.personal.language;

import com.kehua.energy.monitor.app.model.APPModel;

import javax.inject.Inject;

import me.walten.fastgo.di.scope.ActivityScope;

@ActivityScope
public class LanguagePresenter extends LanguageContract.Presenter {

    LanguageContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public LanguagePresenter() {
    }

    @Override
    public void attachView(LanguageContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }
}
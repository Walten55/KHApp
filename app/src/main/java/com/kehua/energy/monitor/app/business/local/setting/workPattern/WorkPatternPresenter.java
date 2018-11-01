package com.kehua.energy.monitor.app.business.local.setting.workPattern;

import com.kehua.energy.monitor.app.model.APPModel;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.scope.ActivityScope;

@ActivityScope
public class WorkPatternPresenter extends WorkPatternContract.Presenter {

    WorkPatternContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public WorkPatternPresenter() {
    }

    @Override
    public void attachView(WorkPatternContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }
}
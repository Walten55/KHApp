package com.kehua.energy.monitor.app.business.home;

import com.kehua.energy.monitor.app.model.APPModel;

import javax.inject.Inject;

import me.walten.fastgo.di.scope.FragmentScope;

@FragmentScope
public class HomePresenter extends HomeContract.Presenter {

    HomeContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public HomePresenter() {
    }

    @Override
    public void attachView(HomeContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

}
package com.kehua.energy.monitor.app.business.local.data;

import com.kehua.energy.monitor.app.model.APPModel;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.scope.FragmentScope;

@FragmentScope
public class DataInfoPresenter extends DataInfoContract.Presenter {

    DataInfoContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public DataInfoPresenter() {
    }

    @Override
    public void attachView(DataInfoContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }
}
package com.kehua.energy.monitor.app.business.favorite;

import com.kehua.energy.monitor.app.model.APPModel;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.scope.FragmentScope;

@FragmentScope
public class FavoritePresenter extends FavoriteContract.Presenter {

    FavoriteContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public FavoritePresenter() {
    }

    @Override
    public void attachView(FavoriteContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }
}
package com.kehua.energy.monitor.app.business.local.LocalMain;

import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.model.APPModel;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.walten.fastgo.di.scope.ActivityScope;

import static com.kehua.energy.monitor.app.configuration.Frame.单相协议;

@ActivityScope
public class LocalMainPresenter extends LocalMainContract.Presenter {

    LocalMainContract.View mView;

    @Inject
    APPModel mModel;

    private Disposable mCollDisposable;

    @Inject
    public LocalMainPresenter() {
    }

    @Override
    public void attachView(LocalMainContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
        //结束采集
        if(mCollDisposable!=null)
            mCollDisposable.dispose();
    }

    @Override
    public void startCollecting() {
        if(mCollDisposable==null||mCollDisposable.isDisposed()){
            collecting();
            //15s
            mCollDisposable = Flowable.interval(60*4, TimeUnit.SECONDS)
                    .observeOn(Schedulers.io())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            collecting();
                        }
                    });
        }
    }

    @Override
    public void collecting() {
        if(单相协议 == LocalUserManager.getPn()){
            mModel.getRemoteModel().collectingSinglePhaseProtocol(1);
        }else {
            mModel.getRemoteModel().collectingThreePhaseProtocol(1);
        }
    }

}
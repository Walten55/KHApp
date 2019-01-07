package com.kehua.energy.monitor.app.business.local.LocalMain;

import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.Cmd;
import com.kehua.energy.monitor.app.model.entity.ModbusResponse;
import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.scope.ActivityScope;
import me.walten.fastgo.utils.XToast;

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
            mCollDisposable = Flowable.interval(15, TimeUnit.SECONDS)
                    .observeOn(Schedulers.io())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            //升级中不轮询
                            if(LocalUserManager.IN_THE_UPGRADE)
                                return;
                            collecting();
                        }
                    });
        }
    }

    @Override
    public void collecting() {
        if(单相协议 == LocalUserManager.getPn()){
            mModel.getRemoteModel().collectingSinglePhaseProtocol(LocalUserManager.getDeviceAddress());
        }else {
            mModel.getRemoteModel().collectingThreePhaseProtocol(LocalUserManager.getDeviceAddress());
        }
    }

    @Override
    public void save(int address, int end, int value, final Consumer<Boolean> consumer) {
        mView.startWaiting(Fastgo.getContext().getString(R.string.设置中));
        mModel.getRemoteModel().fdbgMainThread(Cmd.newWriteCmd(LocalUserManager.getDeviceAddress(), address, end, value), new Consumer<ModbusResponse>() {
            @Override
            public void accept(ModbusResponse modbusResponse) throws Exception {
                mView.stopWaiting();
                if (modbusResponse.isSuccess()) {
                    XToast.success(Fastgo.getContext().getString(R.string.设置成功));
                    if (consumer != null) {
                        consumer.accept(true);
                    }
                } else {
                    XToast.error(Fastgo.getContext().getString(R.string.设置失败));
                    if (consumer != null) {
                        consumer.accept(false);
                    }
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.stopWaiting();
                Logger.e(throwable.getMessage());
                XToast.error(Fastgo.getContext().getString(R.string.设置失败));
                if (consumer != null) {
                    consumer.accept(false);
                }
            }
        });
    }
}
package com.kehua.energy.monitor.app.business.local.history;

import android.content.Context;

import com.blankj.utilcode.util.ActivityUtils;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.ModbusResponse;
import com.kehua.energy.monitor.app.model.entity.RecordData;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;

public class HistoryPresenter extends HistoryContract.Presenter {

    HistoryContract.View mView;

    @Inject
    APPModel mModel;

    WeakReference<Context> localContext = new WeakReference<Context>(ActivityUtils.getTopActivity() == null ? Fastgo.getContext() : ActivityUtils.getTopActivity());

    @Inject
    public HistoryPresenter() {
    }

    @Override
    public void attachView(HistoryContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    public void recordCount(final Consumer<Boolean> consumer) {
        mView.startWaiting(localContext.get().getString(R.string.加载中));
        mModel.getRemoteModel().recordCount(LocalUserManager.getDeviceAddress(), new Consumer<ModbusResponse>() {
            @Override
            public void accept(ModbusResponse modbusResponse) throws Exception {
                mView.stopWaiting();
                if(consumer!=null)
                    consumer.accept(true);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.stopWaiting();
                if(consumer!=null)
                    consumer.accept(false);
            }
        });
    }

    @Override
    public void recordConfig(int recordType, int index,int size, Consumer<Boolean> consumer) {
        mModel.getRemoteModel().recordConfig(LocalUserManager.getDeviceAddress(), recordType, index,size,consumer);
    }

    @Override
    public void records(int template, int index,int size,Consumer<List<RecordData>> consumer) {
        mModel.getRemoteModel().records(LocalUserManager.getDeviceAddress(),template,index,consumer);
    }
}
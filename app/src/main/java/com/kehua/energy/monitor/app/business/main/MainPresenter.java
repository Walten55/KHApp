package com.kehua.energy.monitor.app.business.main;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.Cmd;
import com.kehua.energy.monitor.app.model.entity.ModbusResponse;
import com.kehua.energy.monitor.app.model.entity.PointInfo;
import com.kehua.energy.monitor.app.model.local.db.ObjectBox;
import com.kehua.energy.monitor.app.utils.ByteUtils;
import com.kehua.energy.monitor.app.utils.PasswordUtils;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.scope.ActivityScope;

@ActivityScope
public class MainPresenter extends MainContract.Presenter {

    MainContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public MainPresenter() {
    }

    @Override
    public void attachView(MainContract.View view) {
        mView = view;
        RxBus.get().register(this);

    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }


    @Override
    public void setupDatabase() {
        mModel.getLocalModel().setupDatabase();
    }
}
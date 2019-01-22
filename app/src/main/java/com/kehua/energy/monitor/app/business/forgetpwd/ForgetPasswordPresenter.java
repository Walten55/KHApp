package com.kehua.energy.monitor.app.business.forgetpwd;

import android.content.Context;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.StringUtils;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.model.APPModel;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.scope.ActivityScope;
import me.walten.fastgo.utils.XToast;

@ActivityScope
public class ForgetPasswordPresenter extends ForgetPasswordContract.Presenter {

    ForgetPasswordContract.View mView;

    @Inject
    APPModel mModel;





    @Inject
    public ForgetPasswordPresenter() {
    }

    @Override
    public void attachView(ForgetPasswordContract.View view) {
        mView = view;

    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }



}
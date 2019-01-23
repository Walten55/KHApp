package com.kehua.energy.monitor.app.business.forgetpwd.forgetPwdForCode;

import com.hwangjr.rxbus.RxBus;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.model.APPModel;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.scope.ActivityScope;
import me.walten.fastgo.utils.XToast;

public class ForgetPwdForCodePresenter extends ForgetPwdForCodeContract.Presenter {

    ForgetPwdForCodeContract.View mView;

    @Inject
    APPModel mModel;

    private boolean mVerCodeInWaitting = false;

    @Inject
    public ForgetPwdForCodePresenter() {
    }

    @Override
    public void attachView(ForgetPwdForCodeContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    void verCode(String code) {
        // TODO: 2019/1/23
        XToast.info("验证短信验证码-" + code + "-");
        RxBus.get().post(Config.EVENT_CODE_NEW_PASSWORD, "");
    }

    @Override
    void loadVerCode() {
        countDown();
    }

    @Override
    void countDown() {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(Config.WAIT_SECONDS + 1)//限制时长，否则会一直循环下去
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Long, Integer>() {

                    @Override
                    public Integer apply(Long aLong) throws Exception {
                        return Config.WAIT_SECONDS - aLong.intValue();
                    }
                })
                .subscribe(new Observer<Integer>() {
                               @Override
                               public void onSubscribe(Disposable d) {

                               }

                               @Override
                               public void onNext(Integer waitSeconds) {
                                   mVerCodeInWaitting = true;
                                   mView.requestVerCodeOnClickAble(false);
                                   mView.updateRequestCodeText(waitSeconds + "s");
                               }

                               @Override
                               public void onError(Throwable e) {
                                   mVerCodeInWaitting = false;
                                   mView.requestVerCodeOnClickAble(true);
                                   mView.updateRequestCodeText(Fastgo.getContext().getString(R.string.获取验证码));
                               }

                               @Override
                               public void onComplete() {
                                   mVerCodeInWaitting = false;
                                   mView.requestVerCodeOnClickAble(true);
                                   mView.updateRequestCodeText(Fastgo.getContext().getString(R.string.获取验证码));
                               }
                           }
                );
    }
}
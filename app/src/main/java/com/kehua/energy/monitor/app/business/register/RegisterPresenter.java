package com.kehua.energy.monitor.app.business.register;

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
public class RegisterPresenter extends RegisterContract.Presenter {

    RegisterContract.View mView;

    @Inject
    APPModel mModel;

    boolean mVerCodeInWaitting = false;

    @Inject
    public RegisterPresenter() {
    }

    @Override
    public void attachView(RegisterContract.View view) {
        mView = view;

    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }


    @Override
    void loadVerCode(String phoneNum) {
        if (StringUtils.isTrimEmpty(phoneNum)) {
            XToast.error(Fastgo.getContext().getString(R.string.手机号不能为空));
        } else if (!checkPhone(phoneNum)) {
            XToast.error(Fastgo.getContext().getString(R.string.手机号格式错误));
        } else {
            countDown();
        }
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

    @Override
    boolean checkPhone(String phoneNum) {
        if (phoneNum != null && phoneNum.length() == 11 && "1".equals(String.valueOf(phoneNum.charAt(0)))) {
            return true;
        }
        return false;
    }

    @Override
    boolean checkRegistParam(String pn, String email, String phoneNum, String code, String password, boolean agree) {
        boolean result = true;
        if (StringUtils.isTrimEmpty(pn)) {
            XToast.error(Fastgo.getContext().getString(R.string.采集器编码不能为空));
            result = false;
        } else if (StringUtils.isTrimEmpty(email)) {
            XToast.error(Fastgo.getContext().getString(R.string.邮箱不能为空));
            result = false;
        } else if (StringUtils.isTrimEmpty(phoneNum)) {
            XToast.error(Fastgo.getContext().getString(R.string.手机号不能为空));
            result = false;
        } else if (StringUtils.isTrimEmpty(code)) {
            XToast.error(Fastgo.getContext().getString(R.string.验证码不能为空));
            result = false;
        } else if (StringUtils.isTrimEmpty(password)) {
            XToast.error(Fastgo.getContext().getString(R.string.密码不能为空));
            result = false;
        } else if (!agree) {
            XToast.error(Fastgo.getContext().getString(R.string.请先同意用户服务协议));
            result = false;
        } else if (!checkPhone(phoneNum)) {
            XToast.error(Fastgo.getContext().getString(R.string.手机号格式错误));
            result = false;
        } else if (!(email != null && email.matches(Config.REGEX_EMAIL))) {
            XToast.error(Fastgo.getContext().getString(R.string.邮箱格式错误));
            result = false;
        } else if (password.length() < Config.PASSWORD_LENGTH_MIN) {
            XToast.error(Fastgo.getContext().getString(R.string.密码长度必须大于6位字符));
            result = false;
        }
        return result;
    }

    @Override
    void register(String pn, String email, String phoneNum, String code, String password, boolean agree) {
        if (!checkRegistParam(pn, email, phoneNum, code, password, agree)) {
            return;
        }
        // TODO: 2018/8/28  
    }
}
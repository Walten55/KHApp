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

    String mAccount = "";

    boolean mVerCodeInWaitting = false;

    WeakReference<Context> localContext = null;

    @Inject
    public ForgetPasswordPresenter() {
    }

    @Override
    public void attachView(ForgetPasswordContract.View view) {
        mView = view;
        localContext = new WeakReference<Context>(ActivityUtils.getTopActivity() == null ? Fastgo.getContext() : ActivityUtils.getTopActivity());
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }


    @Override
    void saveLocalAccount(String account) {
        mAccount = account;
        if (!mVerCodeInWaitting) {
            boolean result = true;
            if (StringUtils.isTrimEmpty(account)) {
                result = false;
            } else if (!((account.matches(Config.REGEX_MOBILE) && account.length() == 11) || account.matches(Config.REGEX_EMAIL))) {
                result = false;
            }
            mView.requestVerCodeOnClickAble(result);
            mView.updateRequestCodeText(localContext.get().getString(R.string.获取验证码));
        }
    }


    @Override
    boolean checkUpdateParam(String account, String verCode, String newPwd, String confirmPwd) {
        boolean result = true;
        if (StringUtils.isTrimEmpty(account)) {
            XToast.error(localContext.get().getString(R.string.账号不能为空));
            result = false;
        } else if (StringUtils.isTrimEmpty(verCode)) {
            XToast.error(localContext.get().getString(R.string.验证码不能为空));
            result = false;
        } else if (StringUtils.isTrimEmpty(newPwd) || StringUtils.isTrimEmpty(confirmPwd)) {
            XToast.error(localContext.get().getString(R.string.密码不能为空));
            result = false;
        } else if (!((account.matches(Config.REGEX_MOBILE) && account.length() == 11) || account.matches(Config.REGEX_EMAIL))) {
            XToast.error(localContext.get().getString(R.string.手机号或者邮箱格式错误));
            result = false;
        } else if (newPwd.length() < Config.PASSWORD_LENGTH_MIN || confirmPwd.length() < Config.PASSWORD_LENGTH_MIN) {
            XToast.error(localContext.get().getString(R.string.密码长度必须大于6位字符));
            result = false;
        } else if (!confirmPwd.equals(newPwd)) {
            XToast.error(localContext.get().getString(R.string.确认密码与新密码不一致));
            result = false;
        }
        return result;
    }

    @Override
    void loadVerCode() {
        countDown();
        // TODO: 2018/8/29
    }

    @Override
    void updatePassword(String account, String verCode, String newPwd, String confirmPwd) {
        if (!checkUpdateParam(account, verCode, newPwd, confirmPwd)) {
            return;
        }
        // TODO: 2018/8/28
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

                               }

                               @Override
                               public void onComplete() {
                                   mVerCodeInWaitting = false;
                                   saveLocalAccount(mAccount);
                               }
                           }
                );
    }
}
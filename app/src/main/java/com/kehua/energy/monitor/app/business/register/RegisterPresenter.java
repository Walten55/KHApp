package com.kehua.energy.monitor.app.business.register;

import android.content.Context;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.StringUtils;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.model.APPModel;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.scope.ActivityScope;
import me.walten.fastgo.utils.XToast;

@ActivityScope
public class RegisterPresenter extends RegisterContract.Presenter {

    RegisterContract.View mView;

    @Inject
    APPModel mModel;



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
    boolean checkRegistParam(String nickName, String account, String password, boolean agree) {
        boolean result = true;
        if (StringUtils.isTrimEmpty(nickName)) {
            XToast.error(Fastgo.getContext().getString(R.string.昵称不能为空));
            result = false;
        } else if (StringUtils.isTrimEmpty(account)) {
            XToast.error(Fastgo.getContext().getString(R.string.账号不能为空));
            result = false;
        } else if (StringUtils.isTrimEmpty(password)) {
            XToast.error(Fastgo.getContext().getString(R.string.密码不能为空));
            result = false;
        } else if (!agree) {
            XToast.error(Fastgo.getContext().getString(R.string.请先同意用户服务协议));
            result = false;
        } else if (!((account.matches(Config.REGEX_MOBILE) && account.length() == 11) || account.matches(Config.REGEX_EMAIL))) {
            XToast.error(Fastgo.getContext().getString(R.string.手机号或者邮箱格式错误));
            result = false;
        } else if (password.length() < Config.PASSWORD_LENGTH_MIN) {
            XToast.error(Fastgo.getContext().getString(R.string.密码长度必须大于6位字符));
            result = false;
        }
        return result;
    }

    @Override
    void register(String nickName, String account, String password, boolean agree) {
        if (!checkRegistParam(nickName, account, password, agree)) {
            return;
        }
        // TODO: 2018/8/28  
    }
}
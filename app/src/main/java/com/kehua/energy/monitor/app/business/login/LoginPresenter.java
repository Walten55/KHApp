package com.kehua.energy.monitor.app.business.login;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.model.APPModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.scope.ActivityScope;
import me.walten.fastgo.utils.XToast;

@ActivityScope
public class LoginPresenter extends LoginContract.Presenter {

    LoginContract.View mView;

    @Inject
    APPModel mModel;

    List<Platform> mPlatforms = new ArrayList<>();

    @Inject
    public LoginPresenter() {
    }

    @Override
    public void attachView(LoginContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    boolean checkLoginParam(String account, String password) {
        boolean result = true;
        if (StringUtils.isTrimEmpty(account)) {
            XToast.error(Fastgo.getContext().getString(R.string.账号不能为空));
            result = false;
        } else if (StringUtils.isTrimEmpty(password)) {
            XToast.error(Fastgo.getContext().getString(R.string.密码不能为空));
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
    void login(String account, String password) {
        if (!checkLoginParam(account, password)) {
            return;
        }
        // TODO: 2018/8/27  
    }

    @SuppressLint("CheckResult")
    @Override
    void loadPlatforms() {
        Observable.create(new ObservableOnSubscribe<Platform[]>() {

            @Override
            public void subscribe(ObservableEmitter<Platform[]> emitter) throws Exception {
                emitter.onNext(ShareSDK.getPlatformList());
            }
        }).map(new Function<Platform[], List<Platform>>() {

            @Override
            public List<Platform> apply(Platform[] platforms) throws Exception {
                if (platforms != null && platforms.length > 0) {
                    return Arrays.asList(platforms);
                } else {
                    return new ArrayList<Platform>();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Platform>>() {
                    @Override
                    public void accept(List<Platform> platforms) throws Exception {
                        mPlatforms.clear();
                        mPlatforms.addAll(platforms);
                    }
                });
    }

    @Override
    void qqAuthor() {
        dealPlatform(Config.SHARE_QQ_ID, Config.SHARE_QQ_SORT_ID);
    }

    @Override
    void weChatAuthor() {
        dealPlatform(Config.SHARE_WECHAT_ID, Config.SHARE_WECHAT_SORT_ID);
    }

    private void dealPlatform(int targetPlatformId, int targetPlatformSortId) {
        Platform platform = null;
        for (Platform platf : mPlatforms) {
            if (platf.getId() == targetPlatformId && platf.getSortId() == targetPlatformSortId) {
                platform = platf;
                break;
            }
        }

        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Gson gson = new Gson();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Gson gson = new Gson();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Gson gson = new Gson();
            }
        });
//        if (platform.isAuthValid()) {
//            platform.removeAccount(true);
//            return;
//        }
        platform.SSOSetting(true);
        platform.showUser(null);
    }
}
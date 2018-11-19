package com.kehua.energy.monitor.app.business.local.login;

import android.Manifest;
import android.util.ArrayMap;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.StringUtils;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.Collector;
import com.kehua.energy.monitor.app.model.entity.InvInfoList;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.kehua.energy.monitor.app.utils.LanguageUtils;
import com.kehua.energy.monitor.app.utils.PasswordUtils;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.scope.ActivityScope;
import me.walten.fastgo.utils.XToast;

import static com.kehua.energy.monitor.app.application.LocalUserManager.ROLE_FACTORY;
import static com.kehua.energy.monitor.app.application.LocalUserManager.ROLE_NORMAL;
import static com.kehua.energy.monitor.app.application.LocalUserManager.ROLE_OPS;

@ActivityScope
public class LocalLoginPresenter extends LocalLoginContract.Presenter {

    LocalLoginContract.View mView;

    private String mLastKey;

    @Inject
    APPModel mModel;

    private String mSN;

    @Inject
    public LocalLoginPresenter() {
    }

    @Override
    public void attachView(LocalLoginContract.View view) {
        mView = view;
        mModel.getLocalModel().setupDatabase();
        RxBus.get().register(this);
    }

    @Override
    public void detachView() {
        RxBus.get().unregister(this);
        mView = null;
        mModel.destroy();
    }

    /**
     * 本地采集成功后，接受其发送信号进行数据刷新
     */
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Config.EVENT_CODE_SCAN_RESULT)
            }
    )
    public void scanResult(String result) {
        if (result.contains("KEY:") && result.split("KEY:").length == 2) {
            String newKey = result.split("KEY:")[1].trim().replace(" ", "");
            if (newKey.equals(mLastKey)) {
                mModel.getLocalModel().saveCollectorKey(newKey);
                login(ROLE_NORMAL, "");
            } else {
                XToast.error(Fastgo.getContext().getString(R.string.扫描的采集器与连接采集器不匹配));
            }
        } else {
            XToast.error(Fastgo.getContext().getString(R.string.扫描的采集器与连接采集器不匹配));
        }

    }

    @Override
    public void login(final int role, final String password) {
        if (role != ROLE_NORMAL && StringUtils.isEmpty(password)) {
            XToast.error(Fastgo.getContext().getString(R.string.密码不能为空));
            return;
        }

        if (mView != null)
            mView.startWaiting(Fastgo.getContext().getString(R.string.登录中));
        mModel.getRemoteModel().invinfo(new Consumer<InvInfoList>() {
            @Override
            public void accept(final InvInfoList invInfoList) throws Exception {
                if (mView != null)
                    mView.stopWaiting();
                switch (role) {
                    case ROLE_NORMAL:

                        mModel.getRemoteModel().getdev(new Consumer<Collector>() {
                            @Override
                            public void accept(Collector collector) throws Exception {
                                if (!mModel.getLocalModel().hasCollectorKey(mLastKey = collector.getKey())) {

                                    new RxPermissions(ActivityUtils.getTopActivity()).request(Manifest.permission.CAMERA)
                                            .subscribe(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(Boolean granted) throws Exception {
                                                    if (granted) {
                                                        XToast.normal(Fastgo.getContext().getString(R.string.首次连接采集棒需验证采集棒));
                                                        RouterMgr.get().scan();
                                                    } else {
                                                        XToast.error(Fastgo.getContext().getString(R.string.缺少相关权限));
                                                    }
                                                }
                                            });

                                    return;
                                } else {
                                    loginHandler(role, invInfoList);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                XToast.error(Fastgo.getContext().getString(R.string.无法获取设备信息));
                                return;
                            }
                        });

                        break;
                    case ROLE_OPS:
                        if (!LocalUserManager.OPS_PASSWORD.equals(EncryptUtils.encryptMD5ToString(password))) {
                            XToast.error(Fastgo.getContext().getString(R.string.密码错误));
                            return;
                        }

                        loginHandler(role, invInfoList);
                        break;
                    case ROLE_FACTORY:

                        if ((!StringUtils.isEmpty(mSN) && !String.valueOf(PasswordUtils.createPassword(31, mSN)).equals(password))
                                || (StringUtils.isEmpty(mSN) && !password.equals("333"))) {
                            XToast.error(Fastgo.getContext().getString(R.string.密码错误));
                            return;
                        }

                        loginHandler(role, invInfoList);
                        break;
                }


            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Logger.e(throwable.getMessage());
                XToast.error(Fastgo.getContext().getString(R.string.无法获取设备信息));
                RouterMgr.get().hotspot(RouterMgr.TYPE_OFF_NETWORK);
                if (mView != null) {
                    mView.stopWaiting();
                }
            }
        });


    }

    private void loginHandler(int role, InvInfoList data) {
        LocalUserManager.setRole(role);
        //目前默认采集 设备 1v1
        if (data.getInv() != null && data.getInv().size() > 0)
            LocalUserManager.setDeviceAddress(data.getInv().get(0).getAddr());
        RouterMgr.get().localMain();

        mView.finishView();
    }

    @Override
    public void gatherDeviceInfo() {
        mView.startWaiting(Fastgo.getContext().getString(R.string.加载中));

        mModel.getRemoteModel().invinfo(new Consumer<InvInfoList>() {
            @Override
            public void accept(final InvInfoList data) throws Exception {
                if (data.getInv() != null && data.getInv().size() > 0)
                    LocalUserManager.setDeviceAddress(data.getInv().get(0).getAddr());

                mView.canLogin(true);

                mModel.getRemoteModel().snAndDeviceType(LocalUserManager.getDeviceAddress(), new Consumer<ArrayMap<String, Object>>() {
                    @Override
                    public void accept(ArrayMap<String, Object> result) throws Exception {
                        mView.stopWaiting();
                        if (result.containsKey("sn") && result.containsKey("deviceType") && result.containsKey("pn")) {
                            mView.showDeviceInfo(mSN = String.valueOf(result.get("sn")), Frame.getDeviceTypeName((int) result.get("deviceType")));

                            LocalUserManager.setPn((int) result.get("pn"));
                            LocalUserManager.setDeviceType((int) result.get("deviceType"));

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.stopWaiting();
                        Logger.e(throwable.getMessage());
                        XToast.error(Fastgo.getContext().getString(R.string.无法获取设备信息));
                    }
                });

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.stopWaiting();
                mView.canLogin(false);
                XToast.error(Fastgo.getContext().getString(R.string.无法获取设备信息));
            }
        });

    }

    @Override
    public void selectLanguage(String languageName) {
        LanguageUtils.languageSelect(Fastgo.getContext(), LanguageUtils.getLangeValueByName(Fastgo.getContext(), languageName), false);
    }
}
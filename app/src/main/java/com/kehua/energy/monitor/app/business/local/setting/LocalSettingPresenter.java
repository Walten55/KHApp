package com.kehua.energy.monitor.app.business.local.setting;

import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ActivityUtils;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.route.RouterMgr;

import java.util.ArrayList;

import javax.inject.Inject;

import me.walten.fastgo.base.fragment.SimpleFragment;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.scope.FragmentScope;

import static com.kehua.energy.monitor.app.configuration.Frame.isStorageDevice;

@FragmentScope
public class LocalSettingPresenter extends LocalSettingContract.Presenter {

    LocalSettingContract.View mView;

    @Inject
    APPModel mModel;

    Context localContext = ActivityUtils.getTopActivity() == null
            ? Fastgo.getContext() : ActivityUtils.getTopActivity();

    @Inject
    public LocalSettingPresenter() {
    }

    @Override
    public void attachView(LocalSettingContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    public void setupData() {
        ArrayList<String> titleDatas = new ArrayList<>();

        ArrayList<SimpleFragment> fragmentList = new ArrayList<>();

        if (LocalUserManager.getRole() == LocalUserManager.ROLE_NORMAL) {
            //普通权限
            if (LocalUserManager.getPn() == Frame.单相协议) {
                titleDatas.add(localContext.getString(R.string.基本设置));
                if (isStorageDevice(LocalUserManager.getDeviceType()))
                    titleDatas.add(localContext.getString(R.string.电池设置));
                titleDatas.add(localContext.getString(R.string.电网设置));

                fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_BASIC).navigation());
                if (isStorageDevice(LocalUserManager.getDeviceType()))
                    fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_BATTERY).navigation());
                fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_GRID).navigation());
            } else {
                titleDatas.add(localContext.getString(R.string.基本设置));
                titleDatas.add(localContext.getString(R.string.电网设置));

                fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_BASIC).navigation());
                fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_GRID).navigation());
            }
        } else if (LocalUserManager.getRole() == LocalUserManager.ROLE_OPS) {
            //运维权限
            titleDatas.add(localContext.getString(R.string.基本设置));
            titleDatas.add(localContext.getString(R.string.高级设置));
            titleDatas.add(localContext.getString(R.string.电网设置));
            if (isStorageDevice(LocalUserManager.getDeviceType()))
                titleDatas.add(localContext.getString(R.string.电池设置));
            titleDatas.add(localContext.getString(R.string.模式设置));

            fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_BASIC).navigation());
            fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_ADVANCED).navigation());
            fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_GRID).navigation());
            if (isStorageDevice(LocalUserManager.getDeviceType()))
                fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_BATTERY).navigation());
            fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_PATTERN).navigation());
        } else {
            //厂家权限
            titleDatas.add(localContext.getString(R.string.基本设置));
            titleDatas.add(localContext.getString(R.string.高级设置));
            titleDatas.add(localContext.getString(R.string.电网设置));
            if (isStorageDevice(LocalUserManager.getDeviceType()))
                titleDatas.add(localContext.getString(R.string.电池设置));
            titleDatas.add(localContext.getString(R.string.模式设置));
            titleDatas.add(localContext.getString(R.string.校准设置));
            titleDatas.add(localContext.getString(R.string.设备设置));

            fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_BASIC).navigation());
            fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_ADVANCED).navigation());
            fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_GRID).navigation());
            if (isStorageDevice(LocalUserManager.getDeviceType()))
                fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_BATTERY).navigation());
            fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_PATTERN).navigation());
            fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_CALIBRATION).navigation());
            fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING_DEVICE).navigation());
        }

        mView.setupViewPager(titleDatas, fragmentList);
    }
}
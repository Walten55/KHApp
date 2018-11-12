package com.kehua.energy.monitor.app.application;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.kehua.energy.monitor.app.BuildConfig;
import com.kehua.energy.monitor.app.model.local.db.ObjectBox;
import com.kehua.energy.monitor.app.utils.LanguageUtils;
import com.mob.MobSDK;

import io.objectbox.android.AndroidObjectBrowser;
import me.walten.fastgo.delegate.IAppDelegate;

public class AppDelegateImpl implements IAppDelegate {
    @Override
    public void attachBaseContext(@NonNull Context context) {
        MultiDex.install(context);
    }

    @Override
    public void onCreate(@NonNull Application application) {
        ObjectBox.get().init(application);
        if (BuildConfig.DEBUG) {
            new AndroidObjectBrowser(ObjectBox.get().getBoxStore()).start(application);
        }

        ARouter.init(application);

        MobSDK.init(application);

        LanguageUtils.init(application);

        LanguageUtils.wrap(application, LanguageUtils.getTargetLable());
    }

    @Override
    public void onTerminate(@NonNull Application application) {

    }
}
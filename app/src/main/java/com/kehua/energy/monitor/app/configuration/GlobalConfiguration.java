package com.kehua.energy.monitor.app.configuration;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.kehua.energy.monitor.app.application.AppDelegateImpl;
import com.kehua.energy.monitor.app.business.local.LocalMain.LocalMainActivity;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.util.List;
import java.util.concurrent.TimeUnit;

import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.delegate.IAppDelegate;
import me.walten.fastgo.di.module.GlobalConfigModule;
import me.walten.fastgo.di.module.ThirdPartyModule;
import me.walten.fastgo.integration.ConfigModule;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class GlobalConfiguration implements ConfigModule {
    @Override
    public void applyOptions(Context context, GlobalConfigModule.Builder builder) {
        builder.baseurl("http://10.10.10.1");
        builder.loggerConfiguration(new ThirdPartyModule.LoggerConfiguration() {
            @Override
            public void configLogger(Context context, PrettyFormatStrategy.Builder builder) {
                builder.tag("Walten");
                builder.showThreadInfo(false);
                builder.methodCount(1);
            }
        });
        builder.okhttpConfiguration(new ThirdPartyModule.OkhttpConfiguration() {
            @Override
            public void configOkhttp(Context context, OkHttpClient.Builder builder) {
                if (Fastgo.isPrintLog()) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    builder.addInterceptor(loggingInterceptor);
                }
                //设置超时
                builder.connectTimeout(10, TimeUnit.SECONDS);
                builder.readTimeout(60, TimeUnit.SECONDS);
                builder.writeTimeout(60, TimeUnit.SECONDS);
                //错误重连
                builder.retryOnConnectionFailure(true);
            }
        });
    }

    @Override
    public void injectAppLifecycle(Context context, List<IAppDelegate> lifecycles) {
        lifecycles.add(new AppDelegateImpl());
    }

    @Override
    public void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles) {
        lifecycles.add(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activity instanceof LocalMainActivity) {
                    CacheManager.getInstance().destroy();
                }
            }
        });
    }

    @Override
    public void injectFragmentLifecycle(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles) {

    }
}
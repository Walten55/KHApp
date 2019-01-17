package com.kehua.energy.monitor.app.business.map;

import android.app.Activity;
import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface MapForLocationContract {

    interface View extends BaseView {

    }

    abstract class Presenter extends BasePresenter<View> {
        abstract void openLocation(Activity aty, AMap aMap);
    }
}
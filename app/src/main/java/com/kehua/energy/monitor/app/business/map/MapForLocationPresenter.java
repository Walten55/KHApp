package com.kehua.energy.monitor.app.business.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.model.APPModel;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.scope.ActivityScope;
import me.walten.fastgo.utils.XToast;

@ActivityScope
public class MapForLocationPresenter extends MapForLocationContract.Presenter {

    MapForLocationContract.View mView;

    @Inject
    APPModel mModel;

    boolean hasLocaled = false;

    @Inject
    public MapForLocationPresenter() {
    }

    @Override
    public void attachView(MapForLocationContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    void openLocation(Activity aty, final AMap aMap) {
        //相关权限是否缺失
        if (ActivityCompat.checkSelfPermission(aty, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 没有获得授权，申请授权
            new RxPermissions(aty).request(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if (granted) {
                                starLocal(aMap);
                            } else {
                                XToast.error(Fastgo.getContext().getString(R.string.缺少获取定位相关权限));
                            }
                        }
                    });
        } else {
            starLocal(aMap);
        }
    }

    /**
     * 开启定位
     */
    private void starLocal(final AMap aMap) {
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.interval(2000); //设置发起定位请求的时间间隔，单位：毫秒，默认值：1000毫秒，如果传小于1000的任何值将执行单次定位
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setZoomPosition(AMapOptions.LOGO_MARGIN_BOTTOM);
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (!hasLocaled) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.changeLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                    aMap.moveCamera(cameraUpdate);
                    hasLocaled = true;
                }
            }
        });
    }
}
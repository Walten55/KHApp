package com.kehua.energy.monitor.app.business.map;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.PoiItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gyf.barlibrary.ImmersionBar;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import me.walten.fastgo.base.activitiy.MVPActivity;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;

public class MapForLocationActivity extends MVPActivity<MapForLocationPresenter> implements MapForLocationContract.View, AMapLocationListener {

    @BindView(R.id.mapview)
    MapView mMapView;

    //地图控制器对象
    AMap aMap;

    @BindView(R.id.et_search)
    EditText mEtSearch;

    @BindView(R.id.rv_poi)
    RecyclerView mRecyclerView;

    BaseQuickAdapter<PoiItem, BaseViewHolder> mAdapter;


    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_map_for_location;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mPresenter.searchPois(MapForLocationActivity.this, s.toString());
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                }

            }
        });
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        aMap = mMapView.getMap();
//        //初始化定位
//        mLocationClient = new AMapLocationClient(Fastgo.getContext());
//        //设置定位回调监听
//        mLocationClient.setLocationListener(this);
//        //初始化AMapLocationClientOption对象
//        mLocationOption = new AMapLocationClientOption();
//
//        mLocationClient.setLocationOption(mLocationOption);

        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        mPresenter.openLocation(this, aMap);

    }


    @Override
    public void setupComponent(@NonNull AppComponent appComponent) {
        DaggerActivityComponent.builder()
                .appComponent(appComponent)
                .activityModule(new ActivityModule(this))
                .build()
                .inject(this);

    }

    @Override
    protected boolean enableImmersive(ImmersionBar immersionBar) {
        return false;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            LatLng localLatlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            //改变可视区域为指定位置，CameraPosition4个参数分别为位置，缩放级别，目标可视区域倾斜度，可视区域指向方向（正北逆时针算起，0-360）
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(localLatlng, 16, 0, 30));
            aMap.moveCamera(cameraUpdate);//地图移向指定区域
        } else {
            XToast.error(Fastgo.getContext().getString(R.string.定位失败));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mLocationClient != null) {
//            mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务
//        }
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void PoiSearched(List<PoiItem> data) {
        mRecyclerView.setVisibility(View.VISIBLE);
        if (mAdapter == null) {
            mAdapter = new BaseQuickAdapter<PoiItem, BaseViewHolder>(android.R.layout.simple_list_item_1, data) {

                @Override
                protected void convert(BaseViewHolder helper, PoiItem item) {
                    helper.setText(android.R.id.text1, item.getAdName());
                }
            };
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setNewData(data);
        }
    }
}
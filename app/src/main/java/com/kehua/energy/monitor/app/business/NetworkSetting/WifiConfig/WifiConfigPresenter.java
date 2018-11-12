package com.kehua.energy.monitor.app.business.NetworkSetting.WifiConfig;

import android.content.Context;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.business.NetworkSetting.hotspot.HotspotContract;
import com.kehua.energy.monitor.app.business.NetworkSetting.hotspot.HotspotListAdapter;
import com.kehua.energy.monitor.app.business.NetworkSetting.hotspot.HotspotPresenter;
import com.kehua.energy.monitor.app.business.NetworkSetting.hotspot.HotspotView;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.HotspotInfo;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.scope.ActivityScope;
import me.walten.fastgo.utils.XToast;
import retrofit2.Response;

@ActivityScope
public class WifiConfigPresenter extends WifiConfigContract.Presenter implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    WifiConfigContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    HotspotPresenter mHotspotPresenter;

    HotspotListAdapter adapter;

    Context localContext = ActivityUtils.getTopActivity() == null
            ? Fastgo.getContext() : ActivityUtils.getTopActivity();

    @Inject
    public WifiConfigPresenter() {
    }

    @Override
    public void attachView(WifiConfigContract.View view) {
        mView = view;
        mHotspotPresenter.attachView(new HotspotView(){
            @Override
            public void showHotspotList(HotspotListAdapter adapter) {
                if(WifiConfigPresenter.this.adapter == null){
                    WifiConfigPresenter.this.adapter = adapter;
                    WifiConfigPresenter.this.adapter.setOnItemClickListener(WifiConfigPresenter.this);
                    WifiConfigPresenter.this.adapter.setOnItemChildClickListener(WifiConfigPresenter.this);
                    mView.showHotspotList(adapter);
                }
            }
        });
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
        mHotspotPresenter.detachView();
    }

    @Override
    public void pollScan() {
        mHotspotPresenter.pollScan();
    }

    @Override
    public void apset(String ssid, String pwd) {
        if(StringUtils.isEmpty(ssid)){
            XToast.error(localContext.getString(R.string.SSID不能为空));
            return;
        }else if(StringUtils.isEmpty(pwd)){
            XToast.error(localContext.getString(R.string.密码不能为空));
            return;
        }

        mView.startWaiting(localContext.getString(R.string.设置中));
        mModel.getRemoteModel().apset(ssid, pwd, new Consumer<LinkedTreeMap<String,String>>() {
            @Override
            public void accept(LinkedTreeMap<String,String> response) throws Exception {
                mView.stopWaiting();
                if("ok".equals(response.get("status"))){
                    mView.apsetMsg(true);
                }else if("err".equals(response.get("status"))){
                    mView.apsetMsg(false);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.stopWaiting();
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        mView.ssid(((HotspotInfo)adapter.getItem(position)).getSsid());
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        mView.ssid(((HotspotInfo)adapter.getItem(position)).getSsid());
    }
}
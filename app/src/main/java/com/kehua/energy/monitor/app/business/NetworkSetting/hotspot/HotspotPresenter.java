package com.kehua.energy.monitor.app.business.NetworkSetting.hotspot;

import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.business.personal.PersonalPresenter;
import com.kehua.energy.monitor.app.business.personal.PersonalView;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.HotspotInfo;
import com.kehua.energy.monitor.app.model.entity.InvInfoList;
import com.kehua.energy.monitor.app.utils.WiFiUtils;
import com.orhanobut.logger.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;

public class HotspotPresenter extends HotspotContract.Presenter {

    HotspotContract.View mView;

    @Inject
    APPModel mModel;

    private HotspotListAdapter mAdapter;

    private Disposable mScanDisposable;

    private Disposable mLinkedStateDisposable;

    private int mRecentlyNetId = -1;

    @Inject
    PersonalPresenter mPersonalPresenter;

    PersonalView mPersonView;

    Context localContext = ActivityUtils.getTopActivity() == null
            ? Fastgo.getContext() : ActivityUtils.getTopActivity();
    
    @Inject
    public HotspotPresenter() {
    }

    @Override
    public void attachView(HotspotContract.View view) {
        mView = view;

        mPersonView = new PersonalView(){
            @Override
            public void startWaiting(String msg) {
                mView.startWaiting(msg);
            }

            @Override
            public void stopWaiting() {
                mView.stopWaiting();
            }
        };

        mPersonalPresenter.attachView(mPersonView);
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
        if (mScanDisposable != null) {
            mScanDisposable.dispose();
        }
        if (mLinkedStateDisposable != null) {
            mLinkedStateDisposable.dispose();
        }
        mPersonalPresenter.detachView();
    }

    @Override
    public void check() {
        if(!isGpsOPen()){
            mView.showNoGpsDialog();
        }

        if(!WiFiUtils.getInstance().getWifiManager().isWifiEnabled()){
            mView.showOpenWifiDialog();
        }
    }

    @Override
    public void pollLinkedState() {
        mLinkedStateDisposable = Flowable.interval(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@android.support.annotation.NonNull Long aLong) throws Exception {
                        HotspotInfo curHotspotInfo = WiFiUtils.getInstance().getSimpleWifiInfo();
                        if(curHotspotInfo==null)
                            return;

                        if(curHotspotInfo.getSsid().contains("<unknown ssid>")){
                            curHotspotInfo.setSsid(localContext.getString(R.string.采集器未连接));
                        }
                        mView.showHotspotInfo(curHotspotInfo);
                    }
                });
    }

    @Override
    public void pollScan() {
        scan();
        mScanDisposable = Flowable.interval(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@android.support.annotation.NonNull Long aLong) throws Exception {
                        scan();
                    }
                });
    }

    @Override
    public void scan() {
        Observable.create(new ObservableOnSubscribe<List<HotspotInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<HotspotInfo>> e) throws Exception {
                WiFiUtils.getInstance().startScan();
                List<HotspotInfo> hotspotInfoList = WiFiUtils.getInstance().getSimpleWifiList();
                HotspotInfo curHotspotInfo = WiFiUtils.getInstance().getSimpleWifiInfo();

                HotspotInfo temp = null;

                Iterator<HotspotInfo> it = hotspotInfoList.iterator();
                while(it.hasNext()){
                    HotspotInfo info = it.next();

                    if(StringUtils.isEmpty(info.getSsid())){
                        it.remove();
                        continue;
                    }

                    if (info != null && info.getSsid().equals(curHotspotInfo.getSsid().replace("\"", ""))) {
                        temp = info;
                    }
                }

                if (curHotspotInfo != null&&temp != null) {
                    hotspotInfoList.remove(temp);
                }

                if(curHotspotInfo.getSsid().contains("<unknown ssid>")){
                    curHotspotInfo.setSsid(localContext.getString(R.string.采集器未连接));
                }

                Collections.sort(hotspotInfoList,new Comparator<HotspotInfo>(){
                    @Override
                    public int compare(HotspotInfo info1,HotspotInfo info2){
                        return info2.getLevel()-info1.getLevel();
                    }
                });

                e.onNext(hotspotInfoList);
            }
        }).subscribe(new Consumer<List<HotspotInfo>>() {
            @Override
            public void accept(List<HotspotInfo> hotspotInfos) throws Exception {
                if (mAdapter == null) {
                    mAdapter = new HotspotListAdapter(hotspotInfos);
                    mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                        @Override
                        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                            checkWifiConfigExist((HotspotInfo) adapter.getItem(position));
                        }
                    });
                    mView.showHotspotList(mAdapter);
                }

                mAdapter.setNewData(hotspotInfos);
                mAdapter.notifyDataSetChanged();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Logger.e(throwable.getMessage());
            }
        });

    }

    @Override
    public void disconnect() {
        if(WiFiUtils.getInstance().getNetworkId()!=0){
            WiFiUtils.getInstance().disconnectWiFiNetWork( WiFiUtils.getInstance().getNetworkId());
        }
    }

    @Override
    public void checkWifiConfigExist(HotspotInfo info) {
        List<WifiConfiguration> configuration = WiFiUtils.getInstance().getConfiguration();
        if (configuration == null){
            mView.showPasswordDialog(info);
            return;
        }

        int i = -1;
        for (WifiConfiguration config : configuration) {
            i++;
            if (info != null && info.getSsid() != null && info.getSsid().equals(config.SSID.replace("\"", ""))) {
                mRecentlyNetId = WiFiUtils.getInstance().connectConfiguration(i);
                return;
            }
        }

        mView.showPasswordDialog(info);
    }

    @Override
    public void connect(HotspotInfo info, String password) {
        mRecentlyNetId = WiFiUtils.getInstance().addWiFiNetwork(info.getSsid(), password, info.getCapabilitiesType());
    }

    @Override
    public boolean isGpsOPen() {
        LocationManager locationManager
                = (LocationManager) Fastgo.getContext().getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    @Override
    public void invinfo() {
        mPersonalPresenter.invinfo(new Consumer<InvInfoList>() {
            @Override
            public void accept(InvInfoList invInfoList) throws Exception {
                mView.finishView();
            }
        });
    }

}
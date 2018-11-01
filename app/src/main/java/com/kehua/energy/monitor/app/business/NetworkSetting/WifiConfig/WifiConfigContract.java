package com.kehua.energy.monitor.app.business.NetworkSetting.WifiConfig;

import com.kehua.energy.monitor.app.business.NetworkSetting.hotspot.HotspotListAdapter;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface WifiConfigContract {

    interface View extends BaseView {
        void clickSettingButton(android.view.View view);

        void showHotspotList(HotspotListAdapter adapter);

        void ssid(String ssid);

        void apsetMsg(boolean success);
    }

    abstract class Presenter extends BasePresenter<View> {
        abstract public void pollScan();

        abstract public void apset(String sid,String pwd);
    }
}
package com.kehua.energy.monitor.app.business.NetworkSetting.hotspot;

import android.content.Context;

import com.kehua.energy.monitor.app.model.entity.HotspotInfo;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface HotspotContract {

    interface View extends BaseView {
        void clickNetworkSettingButton(android.view.View view);

        void clickNetworkSystemSettingButtion(android.view.View view);

        void clickDisconnectButton(android.view.View view);

        void showHotspotList(HotspotListAdapter adapter);

        void showHotspotInfo(HotspotInfo info);

        void showPasswordDialog(HotspotInfo info);

        void showNoGpsDialog();

        void showOpenWifiDialog();
    }

    abstract class Presenter extends BasePresenter<View> {

        abstract public void check();

        abstract public void pollLinkedState();

        abstract public void pollScan();

        abstract public void scan();

        abstract public void disconnect();

        abstract public void checkWifiConfigExist(HotspotInfo info);

        abstract public void connect(HotspotInfo info, String password);

        abstract public boolean isGpsOPen();

        abstract public void invinfo();
    }
}
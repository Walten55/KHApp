package com.kehua.energy.monitor.app.business.local.login;

import com.kehua.energy.monitor.app.view.ZoomRelativeLayout;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface LocalLoginContract {

    interface View extends BaseView {
        void showDeviceInfo(String sn,String deviceType);

        void login(android.view.View view);

        void switchRole(ZoomRelativeLayout layout);

        void switchDevice(android.view.View view);

        void canLogin(boolean yes);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void login(int role,String password);

        public abstract void gatherDeviceInfo();

        public abstract void selectLanguage(String languageName);

    }
}
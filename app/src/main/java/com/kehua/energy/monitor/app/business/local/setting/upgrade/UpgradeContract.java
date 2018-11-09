package com.kehua.energy.monitor.app.business.local.setting.upgrade;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface UpgradeContract {

    interface View extends BaseView {
        void onUpgrade(String status);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void upload(String path,Consumer<Boolean> consumer);
        public abstract void upgrade();
        public abstract void startUpgrade();
    }
}
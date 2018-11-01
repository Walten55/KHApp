package com.kehua.energy.monitor.app.business.local.first;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface FirstSettingContract {

    interface View extends BaseView {

    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void setPassword(int password,Consumer<Boolean> consumer);
    }
}
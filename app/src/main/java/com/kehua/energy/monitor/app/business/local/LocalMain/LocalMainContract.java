package com.kehua.energy.monitor.app.business.local.LocalMain;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface LocalMainContract {

    interface View extends BaseView {

    }

    abstract class Presenter extends BasePresenter<View> {
        abstract public void startCollecting();
        abstract public void collecting();
    }
}
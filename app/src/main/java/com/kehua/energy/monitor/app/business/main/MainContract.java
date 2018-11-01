package com.kehua.energy.monitor.app.business.main;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface MainContract {

    interface View extends BaseView {

    }

    abstract class Presenter extends BasePresenter<View> {
        abstract public void setupDatabase();
    }
}
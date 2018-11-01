package com.kehua.energy.monitor.app.business.home;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface HomeContract {

    interface View extends BaseView {

    }

    abstract class Presenter extends BasePresenter<View> {

    }
}
package com.kehua.energy.monitor.app.business.local.setting.standard;

import com.kehua.energy.monitor.app.model.entity.Standard;

import java.util.List;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface StandardContract {

    interface View extends BaseView {
        void onSetupData(List<Standard> data);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void setupData();
    }
}
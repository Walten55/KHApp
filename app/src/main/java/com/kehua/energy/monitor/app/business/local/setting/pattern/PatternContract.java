package com.kehua.energy.monitor.app.business.local.setting.pattern;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface PatternContract {

    interface View extends BaseView {
        void onSetupData(List<MultiItemEntity> data);
        void onUpdateData(Object o);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void setupData();
    }
}
package com.kehua.energy.monitor.app.business.local.setting.calibration;

import com.kehua.energy.monitor.app.business.local.setting.grid.GridContract;
import com.kehua.energy.monitor.app.model.entity.SettingEntity;

import java.util.List;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface CalibrationContract {

    interface View extends BaseView {
        void onSetupData(List<SettingEntity> data);
        void onUpdateData(Object o);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void setupData();
    }
}
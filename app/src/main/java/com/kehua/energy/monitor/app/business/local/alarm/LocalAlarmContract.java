package com.kehua.energy.monitor.app.business.local.alarm;

import com.kehua.energy.monitor.app.model.entity.LocalAlarmList;

import java.util.List;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface LocalAlarmContract {

    interface View extends BaseView {
        void showLocalAlarms(LocalAlarmList data);
    }

    abstract class Presenter extends BasePresenter<View> {
        abstract void loadLocalAlarm();
    }
}
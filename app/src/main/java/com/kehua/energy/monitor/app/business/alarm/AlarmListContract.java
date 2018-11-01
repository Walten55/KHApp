package com.kehua.energy.monitor.app.business.alarm;

import com.kehua.energy.monitor.app.model.entity.Alarm;
import com.kehua.energy.monitor.app.model.entity.MenuItem;

import java.util.List;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface AlarmListContract {

    interface View extends BaseView {
        void setStatus(List<MenuItem> status);

        void showAlarms(List<Alarm> data);
    }

    abstract class Presenter extends BasePresenter<View> {
        abstract void loadAlarmStatus();
        abstract void loadAlarms(int menuId);
    }
}
package com.kehua.energy.monitor.app.business.local.monitor;

import com.kehua.energy.monitor.app.model.entity.GroupInfo;
import com.kehua.energy.monitor.app.model.entity.MonitorEntity;
import com.kehua.energy.monitor.app.model.entity.PointInfo;

import java.util.List;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface LocalMonitorContract {

    interface View extends BaseView {
        void setupTabLayout(List<GroupInfo> infos);

        void onSetupData(List<MonitorEntity> data);

        void onSetupPosition( int runningInfoPosition,int deviceInfoPosition);

        void probationExpire(Object o);
        void probationNear(Object o);
        void normal(Object o);
        void locked(Object o);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void setupData();
        public abstract List<GroupInfo> initGroups();
        public abstract List<PointInfo> getPointInfoListWith(String group);
        public abstract PointInfo getPointInfoWith(int address);
        public abstract void save(int address, int end, int value, final Consumer<Boolean> consumer);
    }
}
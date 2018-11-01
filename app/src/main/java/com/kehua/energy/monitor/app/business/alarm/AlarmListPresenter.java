package com.kehua.energy.monitor.app.business.alarm;

import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.Alarm;
import com.kehua.energy.monitor.app.model.entity.MenuItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.walten.fastgo.di.scope.FragmentScope;

@FragmentScope
public class AlarmListPresenter extends AlarmListContract.Presenter {

    AlarmListContract.View mView;

    @Inject
    APPModel mModel;

    List<MenuItem> mAlarmStatuList = new ArrayList<>();

    List<Alarm> mAlarms = new ArrayList<>();

    @Inject
    public AlarmListPresenter() {
    }

    @Override
    public void attachView(AlarmListContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    void loadAlarmStatus() {
        mAlarmStatuList.clear();
        mAlarmStatuList.add(new MenuItem("全部"));
        mAlarmStatuList.add(new MenuItem("发生"));
        mAlarmStatuList.add(new MenuItem("恢复"));

        mView.setStatus(mAlarmStatuList);
    }

    @Override
    void loadAlarms(int menuId) {
        loadAlarmStatus();
        mAlarms.clear();
        mAlarms.add(new Alarm("告警1","电站1","1994-11-06","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536209922460&di=e481f7025eb9d102c79ad1ad3147944e&imgtype=0&src=http%3A%2F%2Fww2.sinaimg.cn%2Fcrop.0.83.800.449.1000.562%2F006ahoQKgw1f72mtqyci6j30m80etjwo.jpg"));
        mAlarms.add(new Alarm("告警2","电站2","1994-12-04","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536209922460&di=e481f7025eb9d102c79ad1ad3147944e&imgtype=0&src=http%3A%2F%2Fww2.sinaimg.cn%2Fcrop.0.83.800.449.1000.562%2F006ahoQKgw1f72mtqyci6j30m80etjwo.jpg"));
        mAlarms.add(new Alarm("告警3","电站3","1995-01-24","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536209922460&di=e481f7025eb9d102c79ad1ad3147944e&imgtype=0&src=http%3A%2F%2Fww2.sinaimg.cn%2Fcrop.0.83.800.449.1000.562%2F006ahoQKgw1f72mtqyci6j30m80etjwo.jpg"));
        mAlarms.add(new Alarm("告警4","电站4","1994-11-06","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536209922460&di=e481f7025eb9d102c79ad1ad3147944e&imgtype=0&src=http%3A%2F%2Fww2.sinaimg.cn%2Fcrop.0.83.800.449.1000.562%2F006ahoQKgw1f72mtqyci6j30m80etjwo.jpg"));
        mAlarms.add(new Alarm("告警5","电站5","1994-12-04","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536209922460&di=e481f7025eb9d102c79ad1ad3147944e&imgtype=0&src=http%3A%2F%2Fww2.sinaimg.cn%2Fcrop.0.83.800.449.1000.562%2F006ahoQKgw1f72mtqyci6j30m80etjwo.jpg"));
        mAlarms.add(new Alarm("告警6","电站6","1995-01-24","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536209922460&di=e481f7025eb9d102c79ad1ad3147944e&imgtype=0&src=http%3A%2F%2Fww2.sinaimg.cn%2Fcrop.0.83.800.449.1000.562%2F006ahoQKgw1f72mtqyci6j30m80etjwo.jpg"));
        mAlarms.add(new Alarm("告警7","电站7","1994-11-06","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536209922460&di=e481f7025eb9d102c79ad1ad3147944e&imgtype=0&src=http%3A%2F%2Fww2.sinaimg.cn%2Fcrop.0.83.800.449.1000.562%2F006ahoQKgw1f72mtqyci6j30m80etjwo.jpg"));
        mAlarms.add(new Alarm("告警8","电站8","1994-12-04","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536209922460&di=e481f7025eb9d102c79ad1ad3147944e&imgtype=0&src=http%3A%2F%2Fww2.sinaimg.cn%2Fcrop.0.83.800.449.1000.562%2F006ahoQKgw1f72mtqyci6j30m80etjwo.jpg"));
        mAlarms.add(new Alarm("告警9","电站9","1995-01-24","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536209922460&di=e481f7025eb9d102c79ad1ad3147944e&imgtype=0&src=http%3A%2F%2Fww2.sinaimg.cn%2Fcrop.0.83.800.449.1000.562%2F006ahoQKgw1f72mtqyci6j30m80etjwo.jpg"));

        mView.showAlarms(mAlarms);
    }
}
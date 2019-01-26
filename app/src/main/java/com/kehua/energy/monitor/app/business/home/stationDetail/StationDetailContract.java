package com.kehua.energy.monitor.app.business.home.stationDetail;

import com.kehua.energy.monitor.app.model.entity.PerData;
import com.kehua.energy.monitor.app.model.entity.StationEntity;

import java.util.List;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface StationDetailContract {

    interface View extends BaseView {

        void showStation(List<StationEntity> data);
    }

    abstract class Presenter extends BasePresenter<View> {

        abstract void loadData();

        abstract List<PerData> dealChartData();
    }
}
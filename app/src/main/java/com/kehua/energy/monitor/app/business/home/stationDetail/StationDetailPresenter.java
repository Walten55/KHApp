package com.kehua.energy.monitor.app.business.home.stationDetail;

import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.StationEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.scope.ActivityScope;

@ActivityScope
public class StationDetailPresenter extends StationDetailContract.Presenter {

    StationDetailContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public StationDetailPresenter() {
    }

    @Override
    public void attachView(StationDetailContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    void loadData() {
        List<StationEntity> data = new ArrayList<>();
        data.add(new StationEntity(StationEntity.OVERVIEW, ""));

        data.add(new StationEntity(StationEntity.LEFT_TITLE, Fastgo.getContext().getString(R.string.运行数据)));
        data.add(new StationEntity(StationEntity.OPERA_DATA, ""));

        data.add(new StationEntity(StationEntity.LEFT_TITLE, Fastgo.getContext().getString(R.string.环境贡献)));
        data.add(new StationEntity(StationEntity.ENVIRONMENT, ""));

        data.add(new StationEntity(StationEntity.LEFT_TITLE, Fastgo.getContext().getString(R.string.设备信息)));
        data.add(new StationEntity(StationEntity.DEVICE_ITEM, ""));
        data.add(new StationEntity(StationEntity.DEVICE_ITEM, ""));
        data.add(new StationEntity(StationEntity.DEVICE_ITEM, ""));

        mView.showStation(data);
    }
}
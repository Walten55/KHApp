package com.kehua.energy.monitor.app.business.local.setting.pattern.patternModelChild;

import com.blankj.utilcode.util.StringUtils;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.PointInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.walten.fastgo.di.scope.ActivityScope;

@ActivityScope
public class LocalPatternChildPresenter extends LocalPatternChildContract.Presenter {

    LocalPatternChildContract.View mView;

    @Inject
    APPModel mModel;

    List<PointInfo> data = new ArrayList<>();

    @Inject
    public LocalPatternChildPresenter() {
    }

    @Override
    public void attachView(LocalPatternChildContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    public boolean dealData(String sGroup) {
        boolean result = false;

        List<PointInfo> oriPointInfos = mModel.getLocalModel().getPointInfosWith(LocalUserManager.getPn(), Frame.模式设置, LocalUserManager.getRoleAuthority(), true);
        List<PointInfo> pointInfos = new ArrayList<>();

        //如果是光储，则展示全部，否则只展示光伏（1）
        if (Frame.isStorageDevice(LocalUserManager.getDeviceType())) {
            pointInfos = oriPointInfos;
        } else {
            for (PointInfo pointInfo : oriPointInfos) {
                if (pointInfo.getDeviceType() == 1) {
                    pointInfos.add(pointInfo);
                }
            }
        }

        if (!StringUtils.isTrimEmpty(sGroup)) {
            data.clear();
            for (PointInfo pointInfo : pointInfos) {
                if (sGroup.equals(pointInfo.getSgroup())) {
                    data.add(pointInfo);
                }
            }
        }

        if (data.size() > 0) {
            result = true;
            mView.setData(data);
        }
        return result;
    }
}
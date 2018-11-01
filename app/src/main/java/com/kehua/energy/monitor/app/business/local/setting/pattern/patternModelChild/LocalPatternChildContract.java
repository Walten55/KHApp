package com.kehua.energy.monitor.app.business.local.setting.pattern.patternModelChild;

import com.kehua.energy.monitor.app.model.entity.PointInfo;

import java.util.List;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface LocalPatternChildContract {

    interface View extends BaseView {
        void setData(List<PointInfo> data);
        void onUpdateData(Object o);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract boolean dealData(String sGroup);
    }
}
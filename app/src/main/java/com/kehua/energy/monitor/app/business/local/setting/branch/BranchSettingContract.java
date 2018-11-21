package com.kehua.energy.monitor.app.business.local.setting.branch;

import android.util.ArrayMap;

import java.util.List;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface BranchSettingContract {

    interface View extends BaseView {
        void showData(List<ArrayMap<String,Boolean>> data);
    }

    abstract class Presenter extends BasePresenter<View> {
        abstract void setupData(String hexValue);
        abstract int getResult(List<ArrayMap<String,Boolean>> data);
    }
}
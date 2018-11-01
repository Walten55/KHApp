package com.kehua.energy.monitor.app.business.local.setting;

import com.kehua.energy.monitor.app.adapter.CommonViewPagerAdapter;

import java.util.ArrayList;

import me.walten.fastgo.base.fragment.SimpleFragment;
import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface LocalSettingContract {

    interface View extends BaseView {
        void setupViewPager(ArrayList<String> titleDatas,ArrayList<SimpleFragment> fragmentList);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void setupData();
    }
}
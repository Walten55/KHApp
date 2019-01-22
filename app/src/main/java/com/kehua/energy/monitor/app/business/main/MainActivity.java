package com.kehua.energy.monitor.app.business.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gyf.barlibrary.ImmersionBar;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.adapter.CommonViewPagerAdapter;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.kehua.energy.monitor.app.view.ViewPagerSlide;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

import butterknife.BindView;
import me.walten.fastgo.base.fragment.SimpleFragment;
import me.walten.fastgo.di.component.AppComponent;

public class MainActivity extends XMVPActivity<MainPresenter> implements MainContract.View {

    @BindView(R.id.view_pager)
    ViewPagerSlide mViewPager;

    @BindView(R.id.bottom_bar)
    BottomBar mBottomBar;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        setFullScreen();
        cancelFullScreen();
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        ArrayList<String> titleDatas = new ArrayList<>();
        titleDatas.add(getString(R.string.主页));
        titleDatas.add(getString(R.string.告警));
        titleDatas.add(getString(R.string.关注));
        titleDatas.add(getString(R.string.我的));

        ArrayList<SimpleFragment> fragmentList = new ArrayList<>();

        fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.HOME).navigation());
        fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.ALARM_LIST).navigation());
        fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.FAVORITE).navigation());
        fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.PERSONAL).navigation());

        CommonViewPagerAdapter myViewPageAdapter = new CommonViewPagerAdapter(getSupportFragmentManager(), titleDatas, fragmentList);
        mViewPager.setAdapter(myViewPageAdapter);
        mViewPager.setSlide(false);

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                mViewPager.setCurrentItem(tabId == R.id.tab_home ? 0 :
                        tabId == R.id.tab_alarm ? 1 :
                                tabId == R.id.tab_favorite ? 2 : 3, false);
            }
        });

        mPresenter.setupDatabase();
    }

    @Override
    public void setupComponent(@NonNull AppComponent appComponent) {
        DaggerActivityComponent.builder()
                .appComponent(appComponent)
                .activityModule(new ActivityModule(this))
                .build()
                .inject(this);

    }

    @Override
    protected boolean enableImmersive(ImmersionBar immersionBar) {
        mImmersionBar = ImmersionBar.with(this);
        return false;
    }

}
package com.kehua.energy.monitor.app.business.local.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.adapter.CommonViewPagerAdapter;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.base.XMVPFragment;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.route.RouterMgr;

import java.util.ArrayList;

import butterknife.BindView;
import me.walten.fastgo.base.fragment.SimpleFragment;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.widget.titlebar.XTitleBar;

@Route(path = RouterMgr.LOCAL_SETTING)
public class LocalSettingFragment extends XMVPFragment<LocalSettingPresenter> implements LocalSettingContract.View, ViewPager.OnPageChangeListener {

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private CommonViewPagerAdapter mAdapter;


    public LocalSettingFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_local_setting;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
//        mViewPager.addOnPageChangeListener(this);
        if(LocalUserManager.getRole() != LocalUserManager.ROLE_NORMAL){
            mTitleBar.getRightTextView().setText(R.string.设备升级);
        }

        mTitleBar.setListener(new XTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if(action == XTitleBar.ACTION_RIGHT_TEXT){
                    RouterMgr.get().localUpgrade();
                }
            }
        });
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.setupData();
    }

    @Override
    public void setupComponent(@NonNull AppComponent appComponent) {
        DaggerFragmentComponent.builder()
                .appComponent(appComponent)
                .fragmentModule(new FragmentModule(this))
                .build()
                .inject(this);

    }

    @Override
    public void setupViewPager(ArrayList<String> titleDatas, ArrayList<SimpleFragment> fragmentList) {
        mAdapter = new CommonViewPagerAdapter(getChildFragmentManager(),titleDatas,fragmentList);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}


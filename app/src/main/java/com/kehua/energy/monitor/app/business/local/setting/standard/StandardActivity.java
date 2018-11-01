package com.kehua.energy.monitor.app.business.local.setting.standard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.widget.titlebar.XTitleBar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.hwangjr.rxbus.RxBus;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.model.entity.Standard;
import com.kehua.energy.monitor.app.route.RouterMgr;

import java.util.List;

@Route(path = RouterMgr.LOCAL_SETTING_STANDARD)
public class StandardActivity extends XMVPActivity<StandardPresenter> implements StandardContract.View, BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private StandardAdapter mStandardAdapter;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_standard;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        setFullScreen();
        cancelFullScreen();

        mTitleBar.setListener(new XTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if(action == XTitleBar.ACTION_LEFT_BUTTON){
                    finish();
                }
            }
        });

        mRecyclerView.setLayoutManager(new GridLayoutManager(this,4));
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.setupData();
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
        immersionBar.statusBarColor(R.color.colorPrimary);
        immersionBar.statusBarDarkFont(true);
        return true;
    }

    @Override
    public void onSetupData(List<Standard> data) {
        if(mStandardAdapter == null){
            mStandardAdapter = new StandardAdapter(data);
            mStandardAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mStandardAdapter);
        }

        mStandardAdapter.setNewData(data);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Standard standard = (Standard) adapter.getItem(position);
        RxBus.get().post(Config.EVENT_CODE_STANDARD_CHOOSED,standard);
        finish();
    }
}
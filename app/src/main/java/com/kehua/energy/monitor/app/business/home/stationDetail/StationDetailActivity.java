package com.kehua.energy.monitor.app.business.home.stationDetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.model.entity.StationEntity;
import com.kehua.energy.monitor.app.route.RouterMgr;

import java.util.List;

import butterknife.BindView;
import me.walten.fastgo.base.activitiy.MVPActivity;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;

@Route(path = RouterMgr.STATION_DETAIL)
public class StationDetailActivity extends MVPActivity<StationDetailPresenter> implements StationDetailContract.View, BaseQuickAdapter.OnItemChildClickListener {

    StationDetailAdapter mAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_station_detail;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(true);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.loadData();
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
        return false;
    }

    @Override
    public void showStation(List<StationEntity> data) {
        if (mAdapter == null) {
            mAdapter = new StationDetailAdapter(data);
            mAdapter.setOnItemChildClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setNewData(data);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        StationEntity item = (StationEntity) adapter.getItem(position);
        //如果是标题被点击
        if (item.getItemType() == StationEntity.LEFT_TITLE) {
            if (Fastgo.getContext().getString(R.string.运行数据).equals((String) item.getData())) {
//                todo 表图界面
            } else if (Fastgo.getContext().getString(R.string.设备).equals((String) item.getData())) {
                RouterMgr.get().scan();
            }
        }
    }
}
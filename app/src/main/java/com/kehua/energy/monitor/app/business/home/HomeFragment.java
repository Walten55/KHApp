package com.kehua.energy.monitor.app.business.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPFragment;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.model.entity.HomeEntity;
import com.kehua.energy.monitor.app.route.RouterMgr;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.walten.fastgo.di.component.AppComponent;

@Route(path = RouterMgr.HOME)
public class HomeFragment extends XMVPFragment<HomePresenter> implements HomeContract.View, BaseQuickAdapter.OnItemClickListener {


    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    HomeAdapter mAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(true);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        List<HomeEntity> list = new ArrayList<>();
        list.add(new HomeEntity(HomeEntity.OVERVIEW, ""));
        list.add(new HomeEntity(HomeEntity.LEFT_TITLE, getString(R.string.电站列表)));
        list.add(new HomeEntity(HomeEntity.POWER_STATION_ITEM, ""));
        list.add(new HomeEntity(HomeEntity.POWER_STATION_ITEM, ""));
        list.add(new HomeEntity(HomeEntity.POWER_STATION_ITEM, ""));
        list.add(new HomeEntity(HomeEntity.LEFT_TITLE, getString(R.string.关注设备)));
        list.add(new HomeEntity(HomeEntity.DEVICE_ITEM, ""));
        list.add(new HomeEntity(HomeEntity.DEVICE_ITEM, ""));
        list.add(new HomeEntity(HomeEntity.DEVICE_ITEM, ""));
        list.add(new HomeEntity(HomeEntity.DEVICE_ITEM, ""));
        mAdapter = new HomeAdapter(list);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

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
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (mAdapter.getItemViewType(position) == HomeEntity.POWER_STATION_ITEM) {
            RouterMgr.get().stationDetail();
        }
    }
}
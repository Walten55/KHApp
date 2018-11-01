package com.kehua.energy.monitor.app.business.local.alarm;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPFragment;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.model.entity.LocalAlarmList;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import me.walten.fastgo.di.component.AppComponent;

@Route(path = RouterMgr.LOCAL_ALARM)
public class LocalAlarmFragment extends XMVPFragment<LocalAlarmPresenter> implements LocalAlarmContract.View {

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mSmartRefreshLayout;

    @BindView(R.id.tv_alarm_commonly_count)
    TextView mTvCommonlyCount;

    @BindView(R.id.tv_alarm_serious_count)
    TextView mTvSeriousCount;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    LocalAlarmAdapter mLocalAlarmAdapter;

    public LocalAlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_local_alarm;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.loadLocalAlarm();
                refreshLayout.finishRefresh(Config.REFRESH_DELAY);
            }
        });
        mSmartRefreshLayout.setEnableLoadMore(false);
        //创建默认的线性LayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(true);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

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
    public void showLocalAlarms(LocalAlarmList data) {
        mTvCommonlyCount.setText(String.valueOf(data.getCommonAlarmCount()));
        mTvSeriousCount.setText(String.valueOf(data.getSeriousAlarmCount()));

        if (mLocalAlarmAdapter == null) {
            mLocalAlarmAdapter = new LocalAlarmAdapter(data.getDeviceDataList());
            mRecyclerView.setAdapter(mLocalAlarmAdapter);
        } else {
            mLocalAlarmAdapter.setNewData(data.getDeviceDataList());
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        RxBus.get().register(this);
        mPresenter.loadLocalAlarm();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        RxBus.get().unregister(this);
    }


    /**
     * 本地采集成功后，接受其发送信号进行数据刷新
     */
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Config.EVENT_CODE_COLLECT_COMPLETE)
            }
    )
    public void poll(Object o) {
        mPresenter.loadLocalAlarm();
    }
}


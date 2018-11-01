package com.kehua.energy.monitor.app.business.alarm;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPFragment;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.model.entity.Alarm;
import com.kehua.energy.monitor.app.model.entity.MenuItem;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.kehua.energy.monitor.app.view.TopRightMenu.TopRightMenu;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import me.walten.fastgo.di.component.AppComponent;

@Route(path = RouterMgr.ALARM_LIST)
public class AlarmListFragment extends XMVPFragment<AlarmListPresenter> implements AlarmListContract.View {

    View mTitleRightCustomView;

    ImageView mIvStatusIcon;

    TopRightMenu mTopRightMenu;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mSmartRefreshLayout;

    @BindView(R.id.tv_alarm_commonly_count)
    TextView mTvCommonlyCount;

    @BindView(R.id.tv_alarm_serious_count)
    TextView mTvSeriousCount;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    AlarmAdapter mAlarmAdapter;


    public AlarmListFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_alarm_list;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mTitleRightCustomView = mTitleBar.getRightCustomView();
        ((TextView) mTitleRightCustomView.findViewById(R.id.tv_text)).setText(getString(R.string.状态));
        mIvStatusIcon = mTitleRightCustomView.findViewById(R.id.iv_icon_right);
        mTitleRightCustomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIvStatusIcon.setImageResource(R.mipmap.icon_up_white);
                mTopRightMenu.showAsDropDown(mTitleRightCustomView, -200, -25);
            }
        });

        //创建默认的线性LayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(true);

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.loadAlarms(0);
                refreshLayout.finishRefresh(Config.REFRESH_DELAY/*,false*/);
            }
        });
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.loadAlarms(0);
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
    public void setStatus(List<MenuItem> status) {
        if (mTopRightMenu == null) {
            mTopRightMenu = new TopRightMenu(getActivity())
                    .setMenuList(status)
                    .setItemOnclickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            // TODO: 2018/9/5
                            mPresenter.loadAlarms(position);
                            mTopRightMenu.dismiss();
                        }
                    }).setOnDismissListener(new TopRightMenu.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            mIvStatusIcon.setImageResource(R.mipmap.icon_down_white);
                        }
                    });

        } else {
            mTopRightMenu.setMenuList(status);
        }
    }

    @Override
    public void showAlarms(List<Alarm> data) {
        if (mAlarmAdapter == null) {
            mAlarmAdapter = new AlarmAdapter(data);
            mRecyclerView.setAdapter(mAlarmAdapter);
        } else {
            mAlarmAdapter.setNewData(data);
        }
    }
}


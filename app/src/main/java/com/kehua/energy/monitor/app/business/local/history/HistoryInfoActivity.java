package com.kehua.energy.monitor.app.business.local.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.gyf.barlibrary.ImmersionBar;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.model.entity.RecordData;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;
import me.walten.fastgo.widget.titlebar.XTitleBar;

@Route(path = RouterMgr.LOCAL_HISTORY_INFO)
public class HistoryInfoActivity extends XMVPActivity<HistoryPresenter> implements HistoryContract.View, OnLoadMoreListener {

    @Autowired
    int count;

    @Autowired
    int index = 0;

    @Autowired
    int recordType;

    @Autowired
    int template;

    int size;

    private List<RecordData> mData = new ArrayList<>();

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mSmartRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private HistoryAdapter mAdapter;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_history_info;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

        setFullScreen();
        cancelFullScreen();

        mSmartRefreshLayout.setOnLoadMoreListener(this);

        mTitleBar.getCenterTextView().setText(
                recordType == 1?R.string.并脱网记录: recordType == 2?R.string.历史故障
                        : recordType == 3?R.string.用户日志: recordType == 4?R.string.功率调度日志:R.string.并脱网记录);
        mTitleBar.setListener(new XTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == XTitleBar.ACTION_LEFT_BUTTON)
                    finish();
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new HistoryAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        //初始化index、size
        size = count<10?count:10;
        if(size==10){
            index = count-10;
        }else {
            index = 0;
        }

        onLoadMore(mSmartRefreshLayout);
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
    public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
        if(mData.size()==0){
            startWaiting(getString(R.string.加载中));
        }

        if(mData.size()<count){
            mPresenter.recordConfig(recordType, index,size, new Consumer<Boolean>() {
                @Override
                public void accept(Boolean success) throws Exception {
                    if(success){
                        mPresenter.records(template, index,size, new Consumer<List<RecordData>>() {
                            @Override
                            public void accept(List<RecordData> recordData) throws Exception {
                                stopWaiting();
                                refreshLayout.finishLoadMore();
                                if(recordData == null){
                                    XToast.normal(getString(R.string.无更多记录));
                                }else {
                                    mData.addAll(mData.size(),recordData);
                                    mAdapter.setNewData(mData);

                                    size = count-mData.size()<10?count-mData.size():10;
                                    if(size==10){
                                        index-=10;
                                    }else {
                                        index = 0;
                                    }
                                }
                            }
                        });
                    }else {
                        stopWaiting();
                        refreshLayout.finishLoadMore();
                    }
                }
            });
        }else {
            stopWaiting();
            refreshLayout.finishLoadMore();
            XToast.normal(getString(R.string.无更多记录));
        }
    }
}
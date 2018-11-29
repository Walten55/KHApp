package com.kehua.energy.monitor.app.business.local.monitor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ConvertUtils;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPFragment;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.model.entity.GroupInfo;
import com.kehua.energy.monitor.app.model.entity.MonitorEntity;
import com.kehua.energy.monitor.app.route.RouterMgr;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;

@Route(path = RouterMgr.LOCAL_MONITOR)
public class LocalMonitorFragment extends XMVPFragment<LocalMonitorPresenter> implements LocalMonitorContract.View {

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private LocalMonitorAdapter monitorAdapter;

    private int runningInfoPosition = 0;

    private int deviceInfoPosition = 0;

    @BindView(R.id.tv_top_message)
    TextView mTopMessageView;
    /**
     * 选中监听
     */
    private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            RecyclerView.LayoutManager manager = mRecyclerView.getLayoutManager();//获取LayoutManager
            //经过测试LinearLayoutManager和GridLayoutManager有以下的方法,这里只针对LinearLayoutManager
            if (manager instanceof LinearLayoutManager) {
                //第一个可见的位置
                int findFirstVisibleItemPosition = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
                //此方法常用作判断是否能下拉刷新，来解决滑动冲突
                int findFirstCompletelyVisibleItemPosition = ((LinearLayoutManager) manager).findFirstCompletelyVisibleItemPosition();

                if (tab.getPosition() == 0 && findFirstVisibleItemPosition >= runningInfoPosition) {
                    ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(0, 0);
                } else if (tab.getPosition() == 1 && (findFirstVisibleItemPosition < runningInfoPosition || findFirstVisibleItemPosition >= deviceInfoPosition)) {
                    ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(runningInfoPosition, 0);
                } else if (tab.getPosition() == 2 && findFirstVisibleItemPosition < deviceInfoPosition) {
                    ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(deviceInfoPosition, 0);
                }

            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    /**
     * 处理tablayout指示器长度
     */
    private Runnable handlerIndicator = new Runnable() {
        @Override
        public void run() {
            try {
                //拿到tabLayout的mTabStrip属性
                LinearLayout mTabStrip = (LinearLayout) mTabLayout.getChildAt(0);

                int dp40 = ConvertUtils.dp2px(40);
                int dp32 = ConvertUtils.dp2px(32);

                for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                    View tabView = mTabStrip.getChildAt(i);

                    //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                    Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                    mTextViewField.setAccessible(true);

                    TextView mTextView = (TextView) mTextViewField.get(tabView);

                    tabView.setPadding(0, 0, 0, 0);

                    //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                    int width = 0;
                    width = mTextView.getWidth();
                    if (width == 0) {
                        mTextView.measure(0, 0);
                        width = mTextView.getMeasuredWidth();
                    }

                    //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                    params.width = width;
                    params.leftMargin = dp32;
                    params.rightMargin = dp32;
                    tabView.setLayoutParams(params);

                    tabView.invalidate();
                }

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    };
    /**
     * 滑动监听
     */
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();//获取LayoutManager
            //经过测试LinearLayoutManager和GridLayoutManager有以下的方法,这里只针对LinearLayoutManager
            if (manager instanceof LinearLayoutManager) {
                //第一个可见的位置
                int findFirstVisibleItemPosition = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
                //此方法常用作判断是否能下拉刷新，来解决滑动冲突
                int findFirstCompletelyVisibleItemPosition = ((LinearLayoutManager) manager).findFirstCompletelyVisibleItemPosition();

                if (findFirstVisibleItemPosition >= deviceInfoPosition) {
                    mTabLayout.getTabAt(2).select();
                } else if (findFirstVisibleItemPosition >= runningInfoPosition && findFirstVisibleItemPosition < deviceInfoPosition) {
                    mTabLayout.getTabAt(1).select();
                } else if (findFirstVisibleItemPosition == 0) {
                    mTabLayout.getTabAt(0).select();
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    public LocalMonitorFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_local_monitor;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(true);
        mRecyclerView.addOnScrollListener(onScrollListener);

        mTabLayout.setTabTextColors(ContextCompat.getColor(Fastgo.getContext(), R.color.gray), ContextCompat.getColor(Fastgo.getContext(), R.color.colorPrimary));
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(Fastgo.getContext(), R.color.colorPrimary));
        mTabLayout.addOnTabSelectedListener(onTabSelectedListener);
        //线的宽度是根据 tabView的宽度来设置的
        mTabLayout.post(handlerIndicator);
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
    public void setupTabLayout(List<GroupInfo> infos) {

    }

    @Override
    public void onSetupData(List<MonitorEntity> data) {
        if (monitorAdapter == null) {
            monitorAdapter = new LocalMonitorAdapter(data);
            View view = LayoutInflater.from(Fastgo.getContext()).inflate(R.layout.item_local_monitor_overview, null);
            mRecyclerView.setAdapter(monitorAdapter);
        }

        monitorAdapter.setNewData(data);
    }

    @Override
    public void onSetupPosition(int runningInfoPosition, int deviceInfoPosition) {
        this.runningInfoPosition = runningInfoPosition;
        this.deviceInfoPosition = deviceInfoPosition;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (monitorAdapter != null) {
            monitorAdapter.destroy();
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        RxBus.get().register(this);

        mPresenter.setupData();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        RxBus.get().unregister(this);
    }


    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Config.EVENT_CODE_COLLECT_COMPLETE)
            }
    )
    public void collectComplete(Object o) {
        mPresenter.setupData();
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Config.EVENT_CODE_PROBATION_EXPIRE)
            }
    )
    @Override
    public void probationExpire(Object o) {
        mTopMessageView.setText(R.string.试用期到期);
        mTopMessageView.setVisibility(View.VISIBLE);
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Config.EVENT_CODE_PROBATION_NEAR)
            }
    )
    @Override
    public void probationNear(Object o) {
        if(CacheManager.getInstance().get(Frame.试用状态地址())!=null
                &&CacheManager.getInstance().get(Frame.试用状态地址()).getIntValue()==1){
            //试用期状态内

            String message = getString(R.string.试用期临近);
            DeviceData deviceData = CacheManager.getInstance().get(4874);
            if(deviceData!=null){
                int hour = deviceData.getIntValue();
                int handleHour = hour;
                int handleDay = 0;
                if(hour>=24){
                    handleDay = hour/24;
                    handleHour = hour%24;
                }
                if(handleDay>0){
                    message+=(","+getString(R.string.试用期剩余时间)+":"
                            +handleDay
                            +getString(R.string.天)
                            +handleHour
                            +getString(R.string.小时));
                }else {
                    message+=(","+getString(R.string.试用期剩余时间)+":"
                            +hour
                            +getString(R.string.小时));
                }

            }

            mTopMessageView.setText(message);
            mTopMessageView.setVisibility(View.VISIBLE);
        }else if(CacheManager.getInstance().get(Frame.机器锁定状态地址())!=null
                &&CacheManager.getInstance().get(Frame.机器锁定状态地址()).getIntValue() == 1){
            //机器锁定

            mTopMessageView.setText(R.string.设备已锁定);
            mTopMessageView.setVisibility(View.VISIBLE);
        }else {
            mTopMessageView.setVisibility(View.GONE);
        }

    }
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Config.EVENT_CODE_PROBATION_NORMAL)
            }
    )
    @Override
    public void normal(Object o) {
        mTopMessageView.setVisibility(View.GONE);
    }
}


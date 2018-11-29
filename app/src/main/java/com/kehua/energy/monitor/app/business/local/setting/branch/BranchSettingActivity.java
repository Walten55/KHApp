package com.kehua.energy.monitor.app.business.local.setting.branch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.gyf.barlibrary.ImmersionBar;
import com.hwangjr.rxbus.RxBus;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.route.RouterMgr;

import java.util.List;

import butterknife.BindView;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.widget.titlebar.XTitleBar;

@Route(path = RouterMgr.LOCAL_BRANCH_SETTING)
public class BranchSettingActivity extends XMVPActivity<BranchSettingPresenter> implements BranchSettingContract.View {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Autowired
    int address = 6305;

    @Autowired
    String hexValue;

    private BranchSettingAdapter mAdapter;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_branch_setting;
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
                }else if(action == XTitleBar.ACTION_RIGHT_TEXT){
                    int value = mPresenter.getResult(mAdapter.getData());
                    ArrayMap<String,Integer> result  = new ArrayMap<>();
                    result.put("value",value);
                    result.put("address",address);
                    RxBus.get().post(Config.EVENT_CODE_BRANCH_SET,result);
                    finish();
                }
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mTitleBar.getCenterTextView().setText(address== Frame.支路告警屏蔽地址?getString(R.string.支路告警屏蔽):address==Frame.PV支路使能字地址?getString(R.string.PV支路使能字):"");
        mPresenter.setupData(hexValue);
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
        //immersionBar.statusBarDarkFont(true);
        return false;
    }

    @Override
    public void showData(List<ArrayMap<String,Boolean>> data) {
        if(mAdapter == null){
            mAdapter = new BranchSettingAdapter(data);
            mAdapter.setAddress(address);
            mRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.setNewData(data);
        }

    }
}
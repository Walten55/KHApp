package com.kehua.energy.monitor.app.business.NetworkSetting.WifiConfig;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.KeyboardUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.business.NetworkSetting.hotspot.HotspotListAdapter;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.kehua.energy.monitor.app.utils.AndroidBug5497Workaround;

import butterknife.BindView;
import butterknife.OnClick;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;
import me.walten.fastgo.widget.XEditText;
import me.walten.fastgo.widget.titlebar.XTitleBar;

@Route(path = RouterMgr.WIFI_CONFIG)
public class WifiConfigActivity extends XMVPActivity<WifiConfigPresenter> implements WifiConfigContract.View {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.et_ssid)
    XEditText mSSIDView;

    @BindView(R.id.et_wifi_password)
    XEditText mWifiPasswordView;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_wifi_config;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidBug5497Workaround.assistActivity(this);
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

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.pollScan();
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
        //immersionBar.statusBarColor(R.color.colorPrimary);
        return false;
    }

    @OnClick(R.id.tv_setting)
    @Override
    public void clickSettingButton(View view) {
        mPresenter.apset(mSSIDView.getText().toString(),mWifiPasswordView.getText().toString());
    }

    @Override
    public void showHotspotList(HotspotListAdapter adapter) {
        if(mRecyclerView.getAdapter()==null)
            mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void ssid(String ssid) {
        mSSIDView.setText(ssid);
        mSSIDView.setSelection(mSSIDView.getText().length());
        KeyboardUtils.showSoftInput(mWifiPasswordView);
    }

    @Override
    public void apsetMsg(boolean success) {
        if (success){
            XToast.success(getString(R.string.设置成功));
            finish();
        }else {
            XToast.error(getString(R.string.设置失败请稍后重试));
        }
    }
}
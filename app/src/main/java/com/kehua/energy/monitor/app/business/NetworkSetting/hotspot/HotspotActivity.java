package com.kehua.energy.monitor.app.business.NetworkSetting.hotspot;

import android.Manifest;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.KeyboardUtils;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.flyco.roundview.RoundTextView;
import com.gyf.barlibrary.ImmersionBar;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.dialog.EditPwdDialogFragment;
import com.kehua.energy.monitor.app.model.entity.HotspotInfo;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.kehua.energy.monitor.app.utils.WiFiUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;
import me.walten.fastgo.widget.titlebar.XTitleBar;

import static com.kehua.energy.monitor.app.route.RouterMgr.TYPE_SETTING;

@Route(path = RouterMgr.HOTSPOT)
public class HotspotActivity extends XMVPActivity<HotspotPresenter> implements HotspotContract.View {

    @Autowired
    public int type = TYPE_SETTING;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.tv_ssid)
    TextView mCurSSIDView;
    
    @BindView(R.id.tv_state)
    RoundTextView mStateView;

    @BindView(R.id.tv_link_setting)
    RoundTextView mSubmitView;

    private boolean isConnected;

    private EditPwdDialogFragment mPwdDialog;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_hotspot;
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

        if(type == TYPE_SETTING)
            mSubmitView.setText(getString(R.string.联网设置));
        else
            mSubmitView.setText(R.string.进入本地模式);


    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.check();
        new RxPermissions(this).request(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            mPresenter.pollScan();
                            mPresenter.pollLinkedState();
                        } else {
                            XToast.error(getString(R.string.缺少相关权限));
                        }
                    }
                });
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

    @OnClick(R.id.tv_link_setting)
    @Override
    public void clickNetworkSettingButton(View view) {
        if(isConnected){
            if(type == TYPE_SETTING)
                RouterMgr.get().wifiConfig();
            else{
                mPresenter.invinfo();
            }
        } else
            XToast.error(getString(R.string.采集器未连接));
    }

    @Override
    @OnClick(R.id.tv_sys_setting)
    public void clickNetworkSystemSettingButtion(View view) {
        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
    }

    @OnClick(R.id.tv_state)
    @Override
    public void clickDisconnectButton(View view) {
        if(isConnected){
            final NormalListDialog dialog = new NormalListDialog(this, new String[]{
                    getString(R.string.断开),getString(R.string.取消)
            });
            dialog
                    .isTitleShow(false)//
                    .itemPressColor(ContextCompat.getColor(this,R.color.line))//
                    .itemTextColor(ContextCompat.getColor(this,R.color.text_black))//
                    .itemTextSize(15)//
                    .cornerRadius(2)//
                    .widthScale(0.75f)//
                    .show();

            dialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(final AdapterView<?> parent, View view, int position, long id) {
                    if(position == 0){
                        mPresenter.disconnect();
                    }
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public void showHotspotList(HotspotListAdapter adapter) {
        if(mRecyclerView.getAdapter()==null)
            mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showHotspotInfo(HotspotInfo info) {
        SupplicantState supplicantState = info.getSupplicantState();
        if(supplicantState == SupplicantState.SCANNING||supplicantState == SupplicantState.FOUR_WAY_HANDSHAKE){
            mCurSSIDView.setText(Fastgo.getContext().getString(R.string.采集器未连接));
            mStateView.setVisibility(View.INVISIBLE);
            isConnected = false;
        }else if(supplicantState == SupplicantState.COMPLETED){
            mCurSSIDView.setText(info.getSsid().replace("\"",""));
            mStateView.setText(getString(R.string.断开));
            mStateView.setVisibility(View.VISIBLE);
            isConnected = true;
        }else{
            mCurSSIDView.setText(info.getSsid().replace("\"",""));
            if(Fastgo.getContext().getString(R.string.采集器未连接).equals(info.getSsid().replace("\"",""))){
                mStateView.setVisibility(View.INVISIBLE);
            }else {
                mStateView.setVisibility(View.VISIBLE);
                mStateView.setText(getString(R.string.连接中));
            }
            isConnected = false;
        }
    }

    @Override
    public void showPasswordDialog(final HotspotInfo info) {
        mPwdDialog = new EditPwdDialogFragment();
        mPwdDialog.show(getSupportFragmentManager(), "pwdDialog", new EditPwdDialogFragment.OnEditPwdDialogFragmentListener() {
            @Override
            public void onSubmit(String msg) {
                mPresenter.connect(info,msg);
                KeyboardUtils.hideSoftInput(HotspotActivity.this);
            }
        });
    }

    @Override
    public void showNoGpsDialog() {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.title(getString(R.string.温馨提示))
                .content(getString(R.string.定位权限没打开启是否前往开启))
                .btnText(getString(R.string.取消),getString(R.string.确定))
        .setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    @Override
    public void showOpenWifiDialog() {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.title(getString(R.string.温馨提示))
                .content(getString(R.string.Wifi没有开启是否开启))
                .btnText(getString(R.string.取消),getString(R.string.确定))
                .setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        WiFiUtils.getInstance().openWifi();
                    }
                });
        dialog.show();
    }
}
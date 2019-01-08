package com.kehua.energy.monitor.app.business.local.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.roundview.RoundTextView;
import com.gyf.barlibrary.ImmersionBar;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.kehua.energy.monitor.app.utils.LanguageUtils;
import com.kehua.energy.monitor.app.utils.ViewUtils;
import com.kehua.energy.monitor.app.view.ZoomRelativeLayout;

import butterknife.BindView;
import butterknife.OnClick;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;
import me.walten.fastgo.widget.XEditText;
import me.walten.fastgo.widget.titlebar.XTitleBar;

import static com.kehua.energy.monitor.app.application.LocalUserManager.ROLE_FACTORY;
import static com.kehua.energy.monitor.app.application.LocalUserManager.ROLE_NORMAL;
import static com.kehua.energy.monitor.app.application.LocalUserManager.ROLE_OPS;

@Route(path = RouterMgr.LOCAL_LOGIN)
public class LocalLoginActivity extends XMVPActivity<LocalLoginPresenter> implements LocalLoginContract.View {

    @BindView(R.id.tv_sn)
    TextView mSnView;

//    @BindView(R.id.tv_device_type)
//    TextView mDeviceTypeView;

    @BindView(R.id.et_password)
    XEditText mPasswordView;

    @BindView(R.id.zrl_ops)
    ZoomRelativeLayout opsSwitch;

    @BindView(R.id.zrl_user)
    ZoomRelativeLayout userSwitch;

    @BindView(R.id.zrl_factory)
    ZoomRelativeLayout factorySwitch;

    @BindView(R.id.tv_login)
    RoundTextView mLoginView;

    boolean canLogin = true;

    private int role = ROLE_NORMAL;

    private String[] languageNames;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_local_login;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        setFullScreen();
        cancelFullScreen();

        mTitleBar.setListener(new XTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == XTitleBar.ACTION_RIGHT_TEXT) {
                    final ActionSheetDialog dialog = new ActionSheetDialog(mContext, languageNames, null);
                    dialog.cancelText(mContext.getResources().getString(R.string.取消)).isTitleShow(false).show();

                    dialog.setOnOperItemClickL(new OnOperItemClickL() {
                        @Override
                        public void onOperItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            dialog.dismiss();
                            mPresenter.selectLanguage(languageNames[position]);
                        }
                    });
                }
            }
        });

    }

    @OnClick(R.id.dev_container)
    public void devContainerClick(View view) {
        mPresenter.gatherDeviceInfo();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if (!this.isTaskRoot()) { // 判断当前activity是不是所在任务栈的根
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        languageNames = LanguageUtils.getLanguageNames(this);
        mPresenter.initLanguage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.gatherDeviceInfo();
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
        //immersionBar.statusBarColor(R.color.black);
        //immersionBar.statusBarDarkFont(true);
        return false;
    }

    @Override
    public void showDeviceInfo(String sn, String deviceType) {

//        String _n = LanguageUtils.getSysDefaultLanguage(this).equals(LanguageUtils.Chinese) ? "" : "\n";

        mSnView.setText(getString(R.string.机器编号_冒号) + (sn == null ? "" : sn)
                + "\n" + getString(R.string.设备类型_冒号) + deviceType);
//        mDeviceTypeView.setText(getString(R.string.设备类型_冒号) + _n + deviceType);
    }

    @OnClick(R.id.tv_login)
    @Override
    public void login(View view) {
        if (canLogin)
            mPresenter.login(role, mPasswordView.getText().toString());
        else {
            XToast.error(getString(R.string.无法获取设备信息));
            RouterMgr.get().hotspot(RouterMgr.TYPE_OFF_NETWORK);
        }
    }

    @OnClick({R.id.zrl_ops, R.id.zrl_user, R.id.zrl_factory})
    @Override
    public void switchRole(ZoomRelativeLayout layout) {
        switchStyleChange(opsSwitch, R.mipmap.icon_local_peration);
        switchStyleChange(userSwitch, R.mipmap.icon_local_user);
        switchStyleChange(factorySwitch, R.mipmap.icon_local_firm);

        switch (layout.getId()) {
            case R.id.zrl_ops:
                if (mPasswordView.getVisibility() == View.INVISIBLE)
                    YoYo.with(Techniques.SlideInRight)
                            .duration(300)
                            .playOn(findViewById(R.id.et_password));
                role = ROLE_OPS;
                mPasswordView.setVisibility(View.VISIBLE);
                switchStyleChange(opsSwitch, R.mipmap.icon_local_peration_s);
                mLoginView.setText(getString(R.string.登录));
                break;
            case R.id.zrl_user:
                YoYo.with(Techniques.SlideOutRight)
                        .duration(300)
                        .playOn(findViewById(R.id.et_password));
                role = ROLE_NORMAL;
                mPasswordView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPasswordView.setVisibility(View.INVISIBLE);
                    }
                }, 300);
                switchStyleChange(userSwitch, R.mipmap.icon_local_user_s);
                mLoginView.setText(R.string.立刻进入本地模式);
                break;
            case R.id.zrl_factory:
                if (mPasswordView.getVisibility() == View.INVISIBLE)
                    YoYo.with(Techniques.SlideInRight)
                            .duration(300)
                            .playOn(findViewById(R.id.et_password));
                role = ROLE_FACTORY;
                mPasswordView.setVisibility(View.VISIBLE);
                switchStyleChange(factorySwitch, R.mipmap.icon_local_firm_s);
                mLoginView.setText(getString(R.string.登录));
                break;
        }
    }

    @OnClick(R.id.iv_switch)
    @Override
    public void switchDevice(View view) {
        RouterMgr.get().hotspot(RouterMgr.TYPE_OFF_NETWORK);
    }

    @Override
    public void canLogin(boolean yes) {
        canLogin = yes;
    }

    private void switchStyleChange(ZoomRelativeLayout layout, int res) {
        TextView child = (TextView) layout.getChildAt(0);
        ViewUtils.setDrawableTop(child, res);
    }
}
package com.kehua.energy.monitor.app.business.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.roundview.RoundTextView;
import com.gyf.barlibrary.ImmersionBar;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.route.RouterMgr;

import butterknife.BindView;
import butterknife.OnClick;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.widget.XEditText;

@Route(path = RouterMgr.LOGIN)
public class LoginActivity extends XMVPActivity<LoginPresenter> implements LoginContract.View {

    @BindView(R.id.et_account)
    XEditText mEtAccount;

    @BindView(R.id.et_password)
    XEditText mEtPassword;

    @BindView(R.id.tv_login)
    RoundTextView mRtvLogin;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_login;
    }


    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.loadPlatforms();
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
        immersionBar.statusBarColor(R.color.white);
        return true;
    }


    @OnClick(R.id.tv_login)
    public void login() {
        mPresenter.login(mEtAccount.getTrimmedString(), mEtPassword.getTrimmedString());
    }

    @OnClick(R.id.tv_示例电站)
    public void exampleSite() {

    }

    @OnClick(R.id.tv_register)
    void register() {
        RouterMgr.get().register();
    }

    @OnClick(R.id.tv_forgetpwd)
    void forgetPassword() {
        RouterMgr.get().forgetPassword();
    }

    @OnClick(R.id.iv_login_qq)
    void qqLogin() {
        mPresenter.qqAuthor();
    }

    @OnClick(R.id.iv_login_weixin)
    void weChatLogin() {
       mPresenter.weChatAuthor();
    }

    @OnClick(R.id.iv_login_facebook)
    void facebookLogin() {
        // TODO: 2018/8/31  
    }
}
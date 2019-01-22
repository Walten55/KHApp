package com.kehua.energy.monitor.app.business.register;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.StringUtils;
import com.flyco.roundview.RoundTextView;
import com.gyf.barlibrary.ImmersionBar;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.route.RouterMgr;

import butterknife.BindView;
import butterknife.OnClick;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.widget.XEditText;
import me.walten.fastgo.widget.titlebar.XTitleBar;

@Route(path = RouterMgr.REGISTER)
public class RegisterActivity extends XMVPActivity<RegisterPresenter> implements RegisterContract.View {

    @BindView(R.id.view_input)
    View mInputContainerView;

    @BindView(R.id.et_pn)
    XEditText mEtPn;

    @BindView(R.id.et_email)
    XEditText mEtEmail;

    @BindView(R.id.et_phone)
    XEditText mEtPhone;

    @BindView(R.id.et_vercode)
    XEditText mEtVerCode;

    @BindView(R.id.et_password)
    XEditText mEtPassword;

    @BindView(R.id.checkbox_agreement)
    CheckBox mCheckboxAgreement;

    @BindView(R.id.tv_register)
    RoundTextView mRtvRegistern;


    @BindView(R.id.rtv_load_code)
    RoundTextView mTvRequestVerCode;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        setFullScreen();
        cancelFullScreen();
        mTitleBar.setListener(new XTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == XTitleBar.ACTION_LEFT_BUTTON) {
                    finish();
                }
            }
        });

        KeyboardUtils.registerSoftInputChangedListener(this, new KeyboardUtils.OnSoftInputChangedListener() {
            @Override
            public void onSoftInputChanged(int height) {
                if (height > 0) {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(mInputContainerView, "translationY", 0, -Fastgo.getContext().getResources().getDimension(R.dimen.grid_50));
                    anim.setDuration(150);
                    anim.start();
                } else {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(mInputContainerView, "translationY", -Fastgo.getContext().getResources().getDimension(R.dimen.grid_50), 0);
                    anim.setDuration(150);
                    anim.start();
                }
            }
        });
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

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
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarColor(R.color.white);
        return false;
    }


    @OnClick(R.id.tv_register)
    public void register() {
        mPresenter.register(mEtPn.getTextTrimmed(), mEtEmail.getTextTrimmed(), mEtPhone.getTextTrimmed()
                , mEtVerCode.getTextTrimmed(), mEtPassword.getTextTrimmed(), mCheckboxAgreement.isChecked());
    }

    @OnClick(R.id.view_agreement)
    void agree() {
        mCheckboxAgreement.setChecked(!mCheckboxAgreement.isChecked());
    }

    @OnClick(R.id.rtv_load_code)
    void loadCode() {
        mPresenter.loadVerCode(mEtPhone.getTextTrimmed());
    }

    @Override
    public void requestVerCodeOnClickAble(boolean clickAble) {
        int colorId = clickAble ? R.color.btn_blue_nor : R.color.text_gray;
        mTvRequestVerCode.setClickable(clickAble);
        mTvRequestVerCode.getDelegate().setStrokeColor(ContextCompat.getColor(this, colorId));
        mTvRequestVerCode.setTextColor(ContextCompat.getColor(this, colorId));
    }

    @Override
    public void updateRequestCodeText(String text) {
        mTvRequestVerCode.setText(text);
    }

    @Override
    public void toMain() {
        // TODO: 2018/8/28  
    }
}
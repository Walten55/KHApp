package com.kehua.energy.monitor.app.business.forgetpwd;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.widget.XEditText;
import me.walten.fastgo.widget.titlebar.XTitleBar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.KeyboardUtils;
import com.flyco.roundview.RoundTextView;
import com.gyf.barlibrary.ImmersionBar;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.route.RouterMgr;

@Route(path = RouterMgr.FORGOT_PWD)
public class ForgetPasswordActivity extends XMVPActivity<ForgetPasswordPresenter> implements ForgetPasswordContract.View {

    @BindView(R.id.view_input)
    View mInputContainerView;

    @BindView(R.id.et_account)
    XEditText mEtAccount;

    @BindView(R.id.et_vercode)
    XEditText mEtVerCode;

    @BindView(R.id.tv_获取验证码)
    RoundTextView mTvRequestVerCode;

    @BindView(R.id.et_password_new)
    XEditText mEtPasswordNew;

    @BindView(R.id.et_password_confirm)
    XEditText mEtPasswordConfirm;

    @BindView(R.id.tv_submit)
    RoundTextView mRtvUpdate;


    @Override
    public int getLayoutResId() {
        return R.layout.activity_forget_password;
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
                    ObjectAnimator anim = ObjectAnimator.ofFloat(mInputContainerView, "translationY", 0, -Fastgo.getContext().getResources().getDimension(R.dimen.grid_65));
                    anim.setDuration(150);
                    anim.start();
                } else {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(mInputContainerView, "translationY", -Fastgo.getContext().getResources().getDimension(R.dimen.grid_65), 0);
                    anim.setDuration(150);
                    anim.start();
                }
            }
        });
        checkEtAccount();

    }

    private void checkEtAccount() {
        mEtAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.saveLocalAccount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        immersionBar.statusBarColor(R.color.colorPrimary);
        return true;
    }

    @OnClick(R.id.tv_获取验证码)
    public void requestVerCode() {
        mPresenter.loadVerCode();
    }

    @OnClick(R.id.tv_submit)
    public void updatePassword() {
        mPresenter.updatePassword(mEtAccount.getTrimmedString(), mEtVerCode.getTrimmedString()
                , mEtPasswordNew.getTrimmedString(), mEtPasswordConfirm.getTrimmedString());
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

    }
}
package com.kehua.energy.monitor.app.business.forgetpwd.newPwd;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.roundview.RoundTextView;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.route.RouterMgr;

import butterknife.BindView;
import me.walten.fastgo.base.fragment.MVPFragment;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.widget.XEditText;

@Route(path = RouterMgr.NEW_PASSWORD)
public class NewPwdFragment extends MVPFragment<NewPwdPresenter> implements NewPwdContract.View {

    @BindView(R.id.et_password_new)
    XEditText mXetPwdNew;

    @BindView(R.id.et_password_confirm)
    XEditText mXetPwdConfirm;

    @BindView(R.id.tv_submit)
    RoundTextView mRtvSubmit;

    public NewPwdFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_new_pwd;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

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
}


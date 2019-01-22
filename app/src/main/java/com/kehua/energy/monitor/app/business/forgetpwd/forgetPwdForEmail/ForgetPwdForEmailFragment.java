package com.kehua.energy.monitor.app.business.forgetpwd.forgetPwdForEmail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hwangjr.rxbus.RxBus;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.route.RouterMgr;

import butterknife.OnClick;
import me.walten.fastgo.base.fragment.MVPFragment;
import me.walten.fastgo.di.component.AppComponent;

@Route(path = RouterMgr.FORGET_PWD_FOR_EMAIL)
public class ForgetPwdForEmailFragment extends MVPFragment<ForgetPwdForEmailPresenter> implements ForgetPwdForEmailContract.View {



    public ForgetPwdForEmailFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_forget_pwd_for_email;
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

    @OnClick(R.id.tv_next)
    void onNext() {
        RxBus.get().post(Config.EVENT_CODE_FROGET_PHONECODE, "");
    }
}


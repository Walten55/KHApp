package com.kehua.energy.monitor.app.business.local.data;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPFragment;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.route.RouterMgr;

import me.walten.fastgo.di.component.AppComponent;

@Route(path = RouterMgr.LOCAL_DATA)
public class DataInfoFragment extends XMVPFragment<DataInfoPresenter> implements DataInfoContract.View {


    public DataInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_data_info;
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


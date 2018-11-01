package com.kehua.energy.monitor.app.business.favorite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPFragment;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.route.RouterMgr;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.walten.fastgo.di.component.AppComponent;

@Route(path = RouterMgr.FAVORITE)
public class FavoriteFragment extends XMVPFragment<FavoritePresenter> implements FavoriteContract.View {


    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    FavoriteAdapter mAdapter;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_favorite;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(true);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        List<String> list = new ArrayList<>();
        list.add("");
        list.add("");
        list.add("");
        mAdapter = new FavoriteAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
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


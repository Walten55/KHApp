package com.kehua.energy.monitor.app.business.personal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.roundview.RoundTextView;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPFragment;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import butterknife.OnClick;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.widget.titlebar.XTitleBar;

@Route(path = RouterMgr.PERSONAL)
public class PersonalFragment extends XMVPFragment<PersonalPresenter> implements PersonalContract.View {

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mSmartRefreshLayout;

    View mTitleLeftCustomView;

    RoundTextView mTvNewPoint;

    @BindView(R.id.iv_user_head)
    ImageView mIvUserHead;

    @BindView(R.id.tv_user_name)
    TextView mTvUserName;

    @BindView(R.id.tv_count_total_income)
    TextView mTvIncome;

    @BindView(R.id.tv_count_co2_reduction)
    TextView mTvCo2;

    @BindView(R.id.tv_count_equal_tree_plant)
    TextView mTvTreePlant;

    @BindView(R.id.tv_count_conserve_energy)
    TextView mTvEnergy;

    public PersonalFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_personal;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.loadPersonalMainInfo();
                refreshLayout.finishRefresh(Config.REFRESH_DELAY);
            }
        });
        mSmartRefreshLayout.setEnableLoadMore(false);

        mTitleLeftCustomView = mTitleBar.getLeftCustomView();
        mTvNewPoint = mTitleLeftCustomView.findViewById(R.id.tv_point);
        mTitleLeftCustomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/9/6  
            }
        });

        mTitleBar.setListener(new XTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == XTitleBar.ACTION_RIGHT_TEXT) {
                    // TODO: 2018/9/7  
                }
            }
        });
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


    @OnClick(R.id.xiv_user_data)
    public void toUserData(View view) {

    }

    @OnClick(R.id.xiv_creat_site)
    public void creatSite(View view) {

    }

    @OnClick(R.id.xiv_language)
    public void languageSelect(View view) {
        RouterMgr.get().language();
    }

    @OnClick(R.id.xiv_about)
    public void toAbout(View view) {
        RouterMgr.get().about();
    }

    @OnClick(R.id.xiv_local_mode)
    public void localMode(View view) {
        mPresenter.invinfo();
    }

    @OnClick(R.id.xiv_collection_net)
    public void collectionNet(View view) {
        RouterMgr.get().hotspot(RouterMgr.TYPE_SETTING);
    }
}


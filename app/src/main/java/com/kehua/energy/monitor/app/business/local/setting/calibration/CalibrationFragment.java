package com.kehua.energy.monitor.app.business.local.setting.calibration;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPFragment;
import com.kehua.energy.monitor.app.business.input.InputActivity;
import com.kehua.energy.monitor.app.business.local.setting.CommonSettingAdapter;
import com.kehua.energy.monitor.app.business.local.setting.advanced.AdvancedContract;
import com.kehua.energy.monitor.app.business.local.setting.advanced.AdvancedPresenter;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.model.entity.SettingEntity;
import com.kehua.energy.monitor.app.model.entity.Standard;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.kehua.energy.monitor.app.utils.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;
import me.walten.fastgo.widget.XEditText;

@Route(path = RouterMgr.LOCAL_SETTING_CALIBRATION)
public class CalibrationFragment extends XMVPFragment<CalibrationPresenter> implements CalibrationContract.View, OnRefreshListener, BaseQuickAdapter.OnItemClickListener {

    private CommonSettingAdapter mAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    @Inject
    AdvancedPresenter mAdvancedPresenter;

    public CalibrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RxBus.get().register(this);
        mAdvancedPresenter.attachView(new AdvancedContract.View() {
            @Override
            public void onSetupData(List<SettingEntity> data) {
                //do nothing
            }

            @Override
            public void onUpdateData(Object o) {
                CalibrationFragment.this.onUpdateData(o);
            }

            @Override
            public void showTipDialog(String title, String content, OnBtnClickL onBtnClickL) {
                //do nothing
            }

            @Override
            public void onStandardChoose(Standard standard) {
                //do nothing
            }

            @Override
            public void showTipDialog(int opsStatus, String msg) {
                CalibrationFragment.this.showTipDialog(opsStatus, msg);
            }

            @Override
            public void showTipDialog(String msg) {
                CalibrationFragment.this.showTipDialog(msg);
            }

            @Override
            public void startWaiting(String msg) {
                CalibrationFragment.this.startWaiting(msg);
            }

            @Override
            public void stopWaiting() {
                CalibrationFragment.this.stopWaiting();
            }

            @Override
            public void showToast(int opsStatus, String msg) {
                CalibrationFragment.this.showToast(opsStatus, msg);
            }

            @Override
            public void showToast(String msg) {
                CalibrationFragment.this.showToast(msg);
            }

            @Override
            public void finishView() {
                CalibrationFragment.this.finishView();
            }
        });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_calibration;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.setupData();
    }

    @Override
    public void setupComponent(@NonNull AppComponent appComponent) {
        DaggerFragmentComponent.builder()
                .appComponent(appComponent)
                .fragmentModule(new FragmentModule(this))
                .build()
                .inject(this);

    }

    @Override
    public void onSetupData(List<SettingEntity> data) {
        if (mAdapter == null) {
            mAdapter = new CommonSettingAdapter(data, mAdvancedPresenter);
            mAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
        }

        mAdapter.setNewData(data);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mPresenter.setupData();
        refreshLayout.finishRefresh(1000);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        onUpdateData(null);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.get().unregister(this);
        mAdvancedPresenter.detachView();
    }

    @Override
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE)
            }
    )
    public void onUpdateData(Object o) {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        final SettingEntity item = (SettingEntity) adapter.getItem(position);
        if (!item.getData().getDataType().contains("boolean")) {
            final DeviceData deviceData = CacheManager.getInstance().get(Integer.valueOf(item.getData().getAddress()));

            InputActivity.openInput(getActivity(), new InputActivity.InputConfig() {
                @Override
                public void customSetting(XEditText editText) {

                }

                @Override
                public String getTitle() {
                    return item.getData().getDescription();
                }

                @Override
                public String getOldMsg() {
                    if (deviceData != null) {
                        DeviceData correspondingDeviceData = CacheManager.getInstance().get(Integer.valueOf(deviceData.getSgroup()));
                        if (correspondingDeviceData != null)
                            return correspondingDeviceData.getParseValue();
                    }

                    return null;
                }

                @Override
                public String getHintMsg() {
                    return item.getData().getUnit();
                }

                @Override
                public int getDigits() {
                    if (item.getData().getAccuracy() > -1) {
                        return item.getData().getAccuracy();
                    } else
                        return 0;
                }

                @Override
                public int getInputType() {
                    if (deviceData != null && "string".equals(item.getData().getDataType())) {
                        return InputType.TYPE_CLASS_TEXT;
                    } else if (deviceData != null && ("double".equals(item.getData().getDataType()) || "double_signed".equals(item.getData().getDataType()))) {
                        return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
                    } else {
                        return InputType.TYPE_CLASS_NUMBER;
                    }
                }

                @Override
                public String check(String msg) {
                    return null;
                }

                @Override
                public void onResult(final String msg) {
                    try {

                        if (deviceData != null && !"string".equals(item.getData().getDataType())) {
                            mAdvancedPresenter.save(Integer.valueOf(item.getData().getAddress()), Integer.valueOf(msg.trim()), new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean success) throws Exception {
                                    if (success) {
                                        DeviceData correspondingDeviceData = CacheManager.getInstance().get(Integer.valueOf(deviceData.getSgroup()));
                                        if (correspondingDeviceData != null)
                                            correspondingDeviceData.setParseValue(Utils.parseAccuracy(Integer.valueOf(msg), deviceData.getAccuracy()));
                                        mAdapter.notifyDataSetChanged();
                                    }

                                }
                            });
                        }
                    } catch (Exception e) {
                        XToast.error(getString(R.string.设置失败));
                    }

                }
            });
        }
    }
}


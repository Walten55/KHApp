package com.kehua.energy.monitor.app.business.local.setting.battery;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
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
import com.kehua.energy.monitor.app.configuration.Frame;
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
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;
import me.walten.fastgo.widget.XEditText;

@Route(path = RouterMgr.LOCAL_SETTING_BATTERY)
public class BatteryFragment extends XMVPFragment<BatteryPresenter> implements BatteryContract.View, BaseQuickAdapter.OnItemClickListener, OnRefreshListener {

    private CommonSettingAdapter mAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    @Inject
    AdvancedPresenter mAdvancedPresenter;


    public BatteryFragment() {
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
                BatteryFragment.this.onUpdateData(o);
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
                BatteryFragment.this.showTipDialog(opsStatus, msg);
            }

            @Override
            public void showTipDialog(String msg) {
                BatteryFragment.this.showTipDialog(msg);
            }

            @Override
            public void startWaiting(String msg) {
                BatteryFragment.this.startWaiting(msg);
            }

            @Override
            public void stopWaiting() {
                BatteryFragment.this.stopWaiting();
            }

            @Override
            public void showToast(int opsStatus, String msg) {
                BatteryFragment.this.showToast(opsStatus, msg);
            }

            @Override
            public void showToast(String msg) {
                BatteryFragment.this.showToast(msg);
            }

            @Override
            public void finishView() {
                BatteryFragment.this.finishView();
            }
        });
    }


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_battery;
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
        if(mAdapter!=null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        final SettingEntity item = (SettingEntity) adapter.getItem(position);
        if (item.getData().getAddress().equals(Frame.电池类型地址() + "")) {
            final String[] stringItems = {
                    Fastgo.getContext().getString(R.string.铅酸电池),
                    Fastgo.getContext().getString(R.string.磷酸铁锂电池),
                    Fastgo.getContext().getString(R.string.三元电池),
            };
            final ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems, null);
            dialog.isTitleShow(false).show();

            dialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    mAdvancedPresenter.save(Integer.valueOf(item.getData().getAddress()), position, new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean success) throws Exception {
                            DeviceData deviceData = CacheManager.getInstance().get(Integer.valueOf(item.getData().getAddress()));
                            if (success&&deviceData!=null) {
                                deviceData.setIntValue(position);
                                mAdapter.notifyDataSetChanged();
                            }

                        }
                    });
                    dialog.dismiss();
                }
            });
        }else if (item.getData().getAddress().equals(Frame.充电倍率地址() + "")) {
            final String[] stringItems = {
                    "0.1C",
                    "0.2C",
                    "0.5C",
                    "1C"
            };
            final ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems, null);
            dialog.isTitleShow(false).show();

            dialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    mAdvancedPresenter.save(Integer.valueOf(item.getData().getAddress()), position, new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean success) throws Exception {
                            DeviceData deviceData = CacheManager.getInstance().get(Integer.valueOf(item.getData().getAddress()));
                            if (success&&deviceData!=null) {
                                deviceData.setIntValue(position);
                                mAdapter.notifyDataSetChanged();
                            }

                        }
                    });
                    dialog.dismiss();
                }
            });
        }else if (!item.getData().getDataType().contains("boolean")) {
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
                    return deviceData != null ? deviceData.getParseValue() : null;
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
                    } else if (deviceData != null && ("double".equals(item.getData().getDataType())||"double_signed".equals(item.getData().getDataType()))) {
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
                                        deviceData.setParseValue(Utils.parseAccuracy(Integer.valueOf(msg),deviceData.getAccuracy()));
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


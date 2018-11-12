package com.kehua.energy.monitor.app.business.local.setting.advanced;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.base.XMVPFragment;
import com.kehua.energy.monitor.app.business.input.InputActivity;
import com.kehua.energy.monitor.app.business.local.setting.CommonSettingAdapter;
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

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;
import me.walten.fastgo.widget.XEditText;

@Route(path = RouterMgr.LOCAL_SETTING_ADVANCED)
public class AdvancedFragment extends XMVPFragment<AdvancedPresenter> implements AdvancedContract.View, OnRefreshListener, BaseQuickAdapter.OnItemClickListener, View.OnClickListener {

    private CommonSettingAdapter mAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    private View mFooterViewPassword;

    WeakReference<Context> localContext = new WeakReference<Context>(ActivityUtils.getTopActivity() == null ? Fastgo.getContext() : ActivityUtils.getTopActivity());
    
    public AdvancedFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_advanced;
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
            mAdapter = new CommonSettingAdapter(data, mPresenter);
            mAdapter.setOnItemClickListener(this);
            mAdapter.removeAllFooterView();
            //傻逼发展说改成一起设置 后面又要改成分开设置留下的  简直是傻逼
            //mAdapter.addFooterView(getFooterViewPassword());
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RxBus.get().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.get().unregister(this);
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
        if (item.getData().getAddress().equals(Frame.恢复出厂设置地址() + "")) {
            showTipDialog(getString(R.string.温馨提示), getString(R.string.确认恢复出厂设置), new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    mPresenter.toggle(Frame.恢复出厂设置地址(), true, null);
                }
            });
        } else if (item.getData().getAddress().equals(Frame.清除所有发电量地址 + "")) {
            showTipDialog(getString(R.string.温馨提示), getString(R.string.确认清除所有发电量), new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    mPresenter.toggle(Frame.清除所有发电量地址, true, null);
                }
            });
        } else if (item.getData().getAddress().equals(Frame.清除历史记录地址 + "")) {
            showTipDialog(getString(R.string.温馨提示), getString(R.string.确认清除历史记录), new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    mPresenter.toggle(Frame.清除历史记录地址, true, null);
                }
            });
        }else if (item.getData().getAddress().equals(Frame.清除故障录波地址() + "")) {
            showTipDialog(getString(R.string.温馨提示), getString(R.string.确认清除故障录波), new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    mPresenter.toggle(Frame.清除故障录波地址(), true, null);
                }
            });
        }else if (item.getData().getAddress().equals(Frame.清除拉弧故障地址() + "")) {
            showTipDialog(getString(R.string.温馨提示), getString(R.string.确认清除拉弧故障), new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    mPresenter.toggle(Frame.清除拉弧故障地址(), true, null);
                }
            });
        } else if (item.getData().getAddress().equals(Frame.标准类型地址() + "")) {
            RouterMgr.get().localSettingStandard();
        } else if (item.getData().getAddress().equals("6320") && LocalUserManager.getPn() == Frame.单相协议) {
            //单相 外接传感器
            final String[] stringItems = {
                    localContext.get().getString(R.string.无),
                    localContext.get().getString(R.string.CT),
                    localContext.get().getString(R.string.智能电表)};
            final ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems, null);
            dialog.isTitleShow(false).show();

            dialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    mPresenter.save(Integer.valueOf(item.getData().getAddress()), position, new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean success) throws Exception {
                            DeviceData deviceData = CacheManager.getInstance().get(Integer.valueOf(item.getData().getAddress()));
                            if (success && deviceData != null) {
                                deviceData.setIntValue(position);
                                mAdapter.notifyDataSetChanged();
                            }

                        }
                    });
                    dialog.dismiss();
                }
            });
        }else if (item.getData().getAddress().equals("6321") && LocalUserManager.getPn() == Frame.单相协议) {
            //单相 CT变化
            final String[] stringItems = {
                    "0-75/5",
                    "1-50/5",
                    "2-40/5",
                    "3-30/5",
                    "4-25/5",
                    "5-20/5"};
            final ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems, null);
            dialog.isTitleShow(false).show();

            dialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    mPresenter.save(Integer.valueOf(item.getData().getAddress()), position, new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean success) throws Exception {
                            DeviceData deviceData = CacheManager.getInstance().get(Integer.valueOf(item.getData().getAddress()));
                            if (success && deviceData != null) {
                                deviceData.setIntValue(position);
                                mAdapter.notifyDataSetChanged();
                            }

                        }
                    });
                    dialog.dismiss();
                }
            });
        }else if (item.getData().getAddress().equals("6303") && LocalUserManager.getPn() == Frame.三相协议) {
            //仅三相协议有 MPPT并联模式
            final String[] stringItems = {
                    localContext.get().getString(R.string.独立),
                    localContext.get().getString(R.string.四路并联),
                    localContext.get().getString(R.string.两路并联)};
            final ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems, null);
            dialog.isTitleShow(false).show();

            dialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    mPresenter.save(Integer.valueOf(item.getData().getAddress()), position, new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean success) throws Exception {
                            DeviceData deviceData = CacheManager.getInstance().get(Integer.valueOf(item.getData().getAddress()));
                            if (success && deviceData != null) {
                                deviceData.setIntValue(position);
                                mAdapter.notifyDataSetChanged();
                            }

                        }
                    });
                    dialog.dismiss();
                }
            });
        } else if (item.getData().getAddress().equals("6310") && LocalUserManager.getPn() == Frame.三相协议) {
            //仅三相协议有 引用机型
            final String[] stringItems = {
                    "SPI50K-B",
                    "SPI60K-B",
                    "SPI50K-BHV",
                    "SPI60K-BHV",
                    "SPI70K-BHV",
                    "SPI80K-BHV"
            };
            final ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems, null);
            dialog.isTitleShow(false).show();

            dialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    mPresenter.save(Integer.valueOf(item.getData().getAddress()), position, new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean success) throws Exception {
                            DeviceData deviceData = CacheManager.getInstance().get(Integer.valueOf(item.getData().getAddress()));
                            if (success && deviceData != null) {
                                deviceData.setIntValue(position);
                                mAdapter.notifyDataSetChanged();
                            }

                        }
                    });
                    dialog.dismiss();
                }
            });
        } else if (!item.getData().getDataType().contains("boolean")) {
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
                    if (item.getData().getAddress().equals(Frame.开机密码地址[0] + "")
                            || item.getData().getAddress().equals(Frame.试用期密码地址[0] + "")) {
                        return null;
                    } else
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
                    } else if (deviceData != null && ("double".equals(item.getData().getDataType()))) {
                        return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
                    }else if(deviceData != null && "double_signed".equals(item.getData().getDataType())){
                        return InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL ;
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

                    if (item.getData().getAddress().equals(Frame.开机密码地址[0] + "")) {

                        int password = Integer.valueOf(msg.trim());
                        if (password > 999999 || password < 100000) {
                            XToast.error(getString(R.string.非法密码));
                        } else
                            mPresenter.save(Integer.valueOf(item.getData().getAddress()), Frame.开机密码地址[1], Integer.valueOf(msg.trim()), null);

                    } else if (item.getData().getAddress().equals(Frame.试用期密码地址[0] + "")) {
                        int password = Integer.valueOf(msg.trim());
                        if (password > 999999 || password < 100000) {
                            XToast.error(getString(R.string.非法密码));
                        } else
                            mPresenter.save(Integer.valueOf(item.getData().getAddress()), Frame.试用期密码地址[1], Integer.valueOf(msg.trim()), null);

                    } else {
                        try {
                            if (deviceData != null && !"string".equals(item.getData().getDataType())) {
                                mPresenter.save(Integer.valueOf(item.getData().getAddress()), Integer.valueOf(msg.trim()), new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean success) throws Exception {
                                        if (success) {
                                            deviceData.setParseValue(Utils.parseAccuracy(Integer.valueOf(msg), deviceData.getAccuracy()));
                                            mAdapter.notifyDataSetChanged();
                                        }

                                    }
                                });
                            }
                        } catch (Exception e) {
                            XToast.error(getString(R.string.设置失败));
                        }
                    }

                }

            });
        }
    }

    @Override
    public void showTipDialog(String title, String content, final OnBtnClickL onBtnClickL) {
        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.content(content).title(title)
                .style(NormalDialog.STYLE_TWO)//
                .titleTextSize(23);
        dialog.btnText(getString(R.string.取消), getString(R.string.确定));
        dialog.titleTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                if (onBtnClickL != null)
                    onBtnClickL.onBtnClick();
            }
        });
        dialog.show();
    }

    @Override
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Config.EVENT_CODE_STANDARD_CHOOSED)
            }
    )
    public void onStandardChoose(final Standard standard) {
        mPresenter.save(Frame.标准类型地址(), standard.getId(), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean success) throws Exception {
                if (success) {
                    DeviceData deviceData = CacheManager.getInstance().get(Frame.标准类型地址());
                    if (deviceData != null) {
                        deviceData.setIntValue(standard.getId());
                        mAdapter.notifyDataSetChanged();
                    }

                }

            }
        });
    }

    private View getFooterViewPassword() {
        if (mFooterViewPassword == null) {
            mFooterViewPassword = LayoutInflater.from(mContext).inflate(R.layout.footer_view_advanced_password, null);
            mFooterViewPassword.findViewById(R.id.rl_power_on_password).setOnClickListener(this);
            mFooterViewPassword.findViewById(R.id.rl_probation_period_password).setOnClickListener(this);
            mFooterViewPassword.findViewById(R.id.tv_submit).setOnClickListener(this);
        }
        return mFooterViewPassword;
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.tv_submit) {

            if (mFooterViewPassword == null)
                return;

            final TextView powerOnPwdTv = mFooterViewPassword.findViewById(R.id.tv_power_on_password);
            final TextView probationPeriodPwdTv = mFooterViewPassword.findViewById(R.id.tv_probation_period_password);
            if (TextUtils.isEmpty(powerOnPwdTv.getText()) || TextUtils.isEmpty(probationPeriodPwdTv.getText())) {
                XToast.error(getString(R.string.开机密码与试用期密码需同时设置));
                return;
            }
            mPresenter.save(Frame.开机密码地址[0], Frame.试用期密码地址[1],
                    new int[]{Integer.valueOf(powerOnPwdTv.getText().toString())
                            , Integer.valueOf(probationPeriodPwdTv.getText().toString())}, new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean success) throws Exception {
                            if (success) {
                                powerOnPwdTv.setText("");
                                probationPeriodPwdTv.setText("");
                            }
                        }
                    });

        } else {

            InputActivity.openInput(getActivity(), new InputActivity.InputConfig() {
                @Override
                public void customSetting(XEditText editText) {

                }

                @Override
                public String getTitle() {
                    if (v.getId() == R.id.rl_probation_period_password)
                        return getString(R.string.试用期密码);
                    else
                        return getString(R.string.开机密码);
                }

                @Override
                public String getOldMsg() {
                    return null;
                }

                @Override
                public String getHintMsg() {
                    return null;
                }

                @Override
                public int getDigits() {
                    return 0;
                }

                @Override
                public int getInputType() {
                    return InputType.TYPE_CLASS_NUMBER;
                }

                @Override
                public String check(String msg) {
                    int password = Integer.valueOf(msg.trim());
                    if (password > 999999 || password < 100000)
                        return getString(R.string.非法密码);
                    else
                        return null;
                }

                @Override
                public void onResult(final String msg) {
                    if (v.getId() == R.id.rl_probation_period_password) {
                        TextView probationPeriodPwdTv = v.findViewById(R.id.tv_probation_period_password);
                        probationPeriodPwdTv.setText(msg);
                    } else {
                        TextView powerOnPwdTv = v.findViewById(R.id.tv_power_on_password);
                        powerOnPwdTv.setText(msg);
                    }

                }
            });
        }

    }
}


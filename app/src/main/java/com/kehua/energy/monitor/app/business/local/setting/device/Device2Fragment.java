package com.kehua.energy.monitor.app.business.local.setting.device;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.StringUtils;
import com.flyco.dialog.listener.OnBtnClickL;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPFragment;
import com.kehua.energy.monitor.app.business.input.InputActivity;
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
import com.kyleduo.switchbutton.SwitchButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;
import me.walten.fastgo.widget.XEditText;

@Route(path = RouterMgr.LOCAL_SETTING_DEVICE)
public class Device2Fragment extends XMVPFragment<DevicePresenter> implements DeviceContract.View, OnRefreshListener {

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.tv_sn)
    TextView mSNTv;

    @BindView(R.id.tv_probation_period_day)
    TextView mProbationPeriodDayTv;

    @BindView(R.id.tv_station_sn)
    TextView mStationSnTv;

    @BindView(R.id.tv_station_no)
    TextView mStationNoTv;

    @BindView(R.id.tv_mac_adr)
    TextView mMacAdrTv;

    @BindView(R.id.tv_model)
    TextView mModelTv;

    @BindView(R.id.sb_power_on_pwd)
    SwitchButton mPowerOnPwdSwitchButton;

    @BindView(R.id.sb_probation_period)
    SwitchButton mProbationPeriodSwitchButton;

    @BindView(R.id.rl_probation_period_day)
    View mProbationPeriodDayContainer;

    @BindView(R.id.tv_reading_7)
    TextView mReadingView7;
    @BindView(R.id.tv_reading_8)
    TextView mReadingView8;

    @Inject
    AdvancedPresenter mAdvancedPresenter;

    public Device2Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdvancedPresenter.attachView(new AdvancedContract.View() {
            @Override
            public void onSetupData(List<SettingEntity> data) {
                //do nothing
            }

            @Override
            public void onUpdateData(Object o) {
                Device2Fragment.this.onUpdateData(o);
            }

            @Override
            public void showTipDialog(String title, String content,final OnBtnClickL onBtnClickL) {
                //do nothing
            }

            @Override
            public void onStandardChoose(Standard standard) {
                //do nothing
            }

            @Override
            public void showTipDialog(int opsStatus, String msg) {
                Device2Fragment.this.showTipDialog(opsStatus, msg);
            }

            @Override
            public void showTipDialog(String msg) {
                Device2Fragment.this.showTipDialog(msg);
            }

            @Override
            public void startWaiting(String msg) {
                Device2Fragment.this.startWaiting(msg);
            }

            @Override
            public void stopWaiting() {
                Device2Fragment.this.stopWaiting();
            }

            @Override
            public void showToast(int opsStatus, String msg) {
                Device2Fragment.this.showToast(opsStatus, msg);
            }

            @Override
            public void showToast(String msg) {
                Device2Fragment.this.showToast(msg);
            }

            @Override
            public void finishView() {
                Device2Fragment.this.finishView();
            }
        });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_device2;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mRefreshLayout.setOnRefreshListener(this);

        mProbationPeriodSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mProbationPeriodDayContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
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
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mPresenter.setupData();
        refreshLayout.finishRefresh(1000);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdvancedPresenter.detachView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.get().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
//        if (CacheManager.getInstance().get(Frame.开机密码功能地址) != null) {
//            mPowerOnPwdSwitchButton.setCheckedImmediatelyNoEvent(CacheManager.getInstance().get(Frame.开机密码功能地址).getIntValue() != Frame.OFF);
//            CacheManager.getInstance().remove(Frame.开机密码功能地址);
//
//            mReadingView2.setVisibility(View.GONE);
//            mPowerOnPwdSwitchButton.setVisibility(View.VISIBLE);
//        }
//
//        if (CacheManager.getInstance().get(Frame.试用期功能地址) != null) {
//            mProbationPeriodSwitchButton.setCheckedImmediately(CacheManager.getInstance().get(Frame.试用期功能地址).getIntValue() != Frame.OFF);
//            CacheManager.getInstance().remove(Frame.试用期功能地址);
//
//            mReadingView3.setVisibility(View.GONE);
//            mProbationPeriodSwitchButton.setVisibility(View.VISIBLE);
//        }
//
//        if (CacheManager.getInstance().get(Frame.串号相关串号地址) != null) {
//            mSNTv.setText(CacheManager.getInstance().get(Frame.串号相关串号地址).getParseValue());
//            mReadingView1.setVisibility(View.GONE);
//        }
//
//        if (CacheManager.getInstance().get(Frame.串号相关试用期天数地址) != null) {
//            mProbationPeriodDayTv.setText(CacheManager.getInstance().get(Frame.串号相关试用期天数地址).getParseValue());
//            mReadingView4.setVisibility(View.GONE);
//        }

        if (CacheManager.getInstance().get(Frame.MAC地址) != null) {
            mMacAdrTv.setText(CacheManager.getInstance().get(Frame.MAC地址).getParseValue());
            mReadingView7.setVisibility(View.GONE);
        }

        if (CacheManager.getInstance().get(Frame.机器型号地址) != null) {
            mModelTv.setText(CacheManager.getInstance().get(Frame.机器型号地址).getParseValue());
            mReadingView8.setVisibility(View.GONE);
        }

//        if (CacheManager.getInstance().get(Frame.站号配置串号地址) != null) {
//            mStationSnTv.setText(CacheManager.getInstance().get(Frame.站号配置串号地址).getParseValue());
//            mReadingView5.setVisibility(View.GONE);
//        }
//
//        if (CacheManager.getInstance().get(Frame.站号配置站号地址) != null) {
//            mStationNoTv.setText(CacheManager.getInstance().get(Frame.站号配置站号地址).getParseValue());
//            mReadingView6.setVisibility(View.GONE);
//        }
    }

    @OnClick(R.id.tv_sn_submit)
    public void onClickSubmitSN(View v) {
        if(TextUtils.isEmpty(mSNTv.getText())){
            XToast.error(getString(R.string.请设置串号相关串号));
        }else if(mProbationPeriodSwitchButton.isChecked()&&TextUtils.isEmpty(mProbationPeriodDayTv.getText())){
            XToast.error(getString(R.string.请设置串号相关试用期天数));
        }else {
            mAdvancedPresenter.save(mSNTv.getText().toString(),
                    mPowerOnPwdSwitchButton.isChecked()?1:0,
                    mProbationPeriodSwitchButton.isChecked()?1:0,
                    mProbationPeriodSwitchButton.isChecked()?Integer.valueOf(mProbationPeriodDayTv.getText().toString()):0, new Consumer<Boolean>() {
                @Override
                public void accept(Boolean success) throws Exception {
                    if(success){
                        mSNTv.setText("");
                        mProbationPeriodDayTv.setText("");
                        mPowerOnPwdSwitchButton.setCheckedImmediatelyNoEvent(false);
                        mProbationPeriodSwitchButton.setCheckedImmediatelyNoEvent(false);
                    }
                }
            });
        }
    }

    @OnClick(R.id.tv_station_submit)
    public void onClickSubmitStation(View v) {
        if(TextUtils.isEmpty(mStationSnTv.getText())){
            XToast.error("请设置\"站号配置-串号\"");
        }else if(TextUtils.isEmpty(mStationSnTv.getText())){
            XToast.error("请设置\"站号配置-站号\"");
        }else {
            mAdvancedPresenter.save(mStationSnTv.getText().toString(), Integer.valueOf(mStationNoTv.getText().toString()), new Consumer<Boolean>() {
                @Override
                public void accept(Boolean success) throws Exception {
                    if(success){
                        mStationSnTv.setText("");
                        mStationNoTv.setText("");
                    }
                }
            });
        }
    }


    @OnClick({
            R.id.rl_sn,
            R.id.rl_probation_period_day,
            R.id.rl_station_sn,
            R.id.rl_station_no,
            R.id.rl_mac_adr,
            R.id.rl_model
    })
    public void onClickItem(View v) {
        DeviceData temp = null;
        TextView tempTv = null;
        switch (v.getId()) {
            case R.id.rl_sn:
                temp = CacheManager.getInstance().get(Frame.串号相关串号地址);
                tempTv = mSNTv;
                break;
            case R.id.rl_probation_period_day:
                temp = CacheManager.getInstance().get(Frame.串号相关试用期天数地址);
                tempTv = mProbationPeriodDayTv;
                break;
            case R.id.rl_station_sn:
                temp = CacheManager.getInstance().get(Frame.站号配置串号地址);
                tempTv = mStationSnTv;
                break;
            case R.id.rl_station_no:
                temp = CacheManager.getInstance().get(Frame.站号配置站号地址);
                tempTv = mStationNoTv;
                break;
            case R.id.rl_mac_adr:
                temp = CacheManager.getInstance().get(Frame.MAC地址);
                tempTv = mMacAdrTv;
                break;
            case R.id.rl_model:
                temp = CacheManager.getInstance().get(Frame.机器型号地址);
                tempTv = mModelTv;
                break;

        }
        final DeviceData deviceData = temp;
        final TextView textView = tempTv;

        if(deviceData==null)
            return;

        InputActivity.openInput(getActivity(), new InputActivity.InputConfig() {
            @Override
            public void customSetting(XEditText editText) {
//                if ((deviceData!=null&&Integer.valueOf(deviceData.getRegisterAddress()) == Frame.串号相关串号地址)||
//                        (deviceData!=null&&Integer.valueOf(deviceData.getRegisterAddress()) == Frame.站号配置串号地址)){
//                    editText.setPattern(new int[]{4,4,4,4,4},"-");
//                }
            }

            @Override
            public String getTitle() {
                return deviceData != null ? deviceData.getDescription() : null;
            }

            @Override
            public String getOldMsg() {
                return textView.getText().toString();
            }

            @Override
            public String getHintMsg() {
                return  deviceData != null ? deviceData.getUnit() : null;
            }

            @Override
            public int getDigits() {
                if (deviceData!=null&&deviceData.getAccuracy() > -1) {
                    return deviceData.getAccuracy();
                } else
                    return 0;
            }

            @Override
            public int getInputType() {
                if (deviceData != null && "string".equals(deviceData.getDataType())) {
                    return InputType.TYPE_CLASS_TEXT;
                } else if (deviceData != null && ("double".equals(deviceData.getDataType())||"double_signed".equals(deviceData.getDataType()))) {
                    return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
                } else {
                    return InputType.TYPE_CLASS_NUMBER;
                }
            }

            @Override
            public String check(String msg) {
                if (Integer.valueOf(deviceData.getRegisterAddress()) == Frame.串号相关串号地址||
                        Integer.valueOf(deviceData.getRegisterAddress()) == Frame.站号配置串号地址) {
                    return msg.length() != 20 ? getString(R.string.串号长度为20位字符) : null;
                } else if (Integer.valueOf(deviceData.getRegisterAddress()) == Frame.站号配置站号地址) {
                    return Integer.valueOf(msg) < 1 || Integer.valueOf(msg) > 247 ? getString(R.string.站号范围) : null;
                }
                return null;
            }

            @Override
            public void onResult(final String msg) {
                if(StringUtils.isEmpty(msg))
                    return;

                if (Integer.valueOf(deviceData.getRegisterAddress()) == Frame.MAC地址||Integer.valueOf(deviceData.getRegisterAddress()) == Frame.机器型号地址) {
                    try {
                        if ( !"string".equals(deviceData.getDataType())) {
                            mAdvancedPresenter.save(Integer.valueOf(deviceData.getRegisterAddress()), Integer.valueOf(msg.replace(".", "").trim()), new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean success) throws Exception {
                                    if (deviceData != null && success) {
                                        textView.setText(msg);
                                    }
                                }
                            });
                        }else {

                            mAdvancedPresenter.save(Integer.valueOf(deviceData.getRegisterAddress()),Integer.valueOf(deviceData.getRegisterAddress())+deviceData.getByteCount()/2-1, msg, new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean success) throws Exception {
                                    if (deviceData != null && success) {
                                        textView.setText(msg);
                                    }

                                }
                            });
                        }
                    } catch (Exception e) {
                        XToast.error(getString(R.string.设置失败));
                    }
                }else {
                    textView.setText(msg);
                }

            }
        });
    }
}


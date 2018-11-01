package com.kehua.energy.monitor.app.business.local.first;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.dialog.listener.OnBtnClickL;
import com.gyf.barlibrary.ImmersionBar;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.business.input.InputActivity;
import com.kehua.energy.monitor.app.business.local.setting.advanced.AdvancedContract;
import com.kehua.energy.monitor.app.business.local.setting.advanced.AdvancedPresenter;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.model.entity.SettingEntity;
import com.kehua.energy.monitor.app.model.entity.Standard;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;
import me.walten.fastgo.widget.XEditText;
import me.walten.fastgo.widget.titlebar.XTitleBar;

@Route(path = RouterMgr.LOCAL_ABOUT_SN)
public class FirstSettingActivity extends XMVPActivity<FirstSettingPresenter> implements FirstSettingContract.View {

    @BindView(R.id.tv_sn)
    TextView mSNTv;

    @BindView(R.id.tv_probation_period_day)
    TextView mProbationPeriodDayTv;

    @BindView(R.id.sb_power_on_pwd)
    SwitchButton mPowerOnPwdSwitchButton;

    @BindView(R.id.sb_probation_period)
    SwitchButton mProbationPeriodSwitchButton;

    @BindView(R.id.rl_probation_period_day)
    View mProbationPeriodDayContainer;

    @Inject
    AdvancedPresenter mAdvancedPresenter;

    @BindView(R.id.et_password)
    XEditText mPasswordView;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_first_setting;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        setFullScreen();
        cancelFullScreen();

        mTitleBar.setListener(new XTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if(action == XTitleBar.ACTION_LEFT_BUTTON){
                    finish();
                }
            }
        });

        mProbationPeriodSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mProbationPeriodDayContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdvancedPresenter.attachView(new AdvancedContract.View() {
            @Override
            public void onSetupData(List<SettingEntity> data) {
                //do nothing
            }

            @Override
            public void onUpdateData(Object o) {
                //do nothing
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
                FirstSettingActivity.this.showTipDialog(opsStatus, msg);
            }

            @Override
            public void showTipDialog(String msg) {
                FirstSettingActivity.this.showTipDialog(msg);
            }

            @Override
            public void startWaiting(String msg) {
                FirstSettingActivity.this.startWaiting(msg);
            }

            @Override
            public void stopWaiting() {
                FirstSettingActivity.this. stopWaiting();
            }

            @Override
            public void showToast(int opsStatus, String msg) {
                FirstSettingActivity.this.showToast(opsStatus, msg);
            }

            @Override
            public void showToast(String msg) {
                FirstSettingActivity.this.showToast(msg);
            }

            @Override
            public void finishView() {
                FirstSettingActivity.this.finishView();
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
        immersionBar.statusBarDarkFont(true);
        return true;
    }

    @OnClick(R.id.tv_sn_submit)
    public void onClickSubmitSN(View v) {
        if(TextUtils.isEmpty(mPasswordView.getText())){
            XToast.warning(Fastgo.getContext().getString(R.string.密码不能为空));
        }else if(!"333".equals(mPasswordView.getText().toString())){
            XToast.error(Fastgo.getContext().getString(R.string.密码错误));
        } else if(TextUtils.isEmpty(mSNTv.getText())){
            XToast.warning(getString(R.string.请设置串号相关串号));
        }else if(mProbationPeriodSwitchButton.isChecked()&&TextUtils.isEmpty(mProbationPeriodDayTv.getText())){
            XToast.warning(getString(R.string.请设置串号相关试用期天数));
        }else {

            mPresenter.setPassword(333, new Consumer<Boolean>() {
                @Override
                public void accept(Boolean success) throws Exception {
                    if(success){
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
                                            finish();
                                        }
                                    }
                                });
                    }else {
                        XToast.error(getString(R.string.密码下发失败));
                    }
                }
            });

        }

    }

    @OnClick({
            R.id.rl_sn,
            R.id.rl_probation_period_day
    })
    public void onClickItem(final View v) {
        final TextView textView = v.getId() == R.id.rl_sn?mSNTv:mProbationPeriodDayTv;

        InputActivity.openInput(this, new InputActivity.InputConfig() {
            @Override
            public void customSetting(XEditText editText) {

            }

            @Override
            public String getTitle() {
                return v.getId() == R.id.rl_sn ? getString(R.string.串号) : getString(R.string.试用期天数);
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
                return v.getId() == R.id.rl_sn ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_NUMBER;
            }

            @Override
            public String check(String msg) {
                if (v.getId() == R.id.rl_sn) {
                    return msg.length() != 20 ? getString(R.string.串号长度为20位字符) : null;
                }
                return null;
            }

            @Override
            public void onResult(final String msg) {
                textView.setText(msg);
            }
        });
    }
}
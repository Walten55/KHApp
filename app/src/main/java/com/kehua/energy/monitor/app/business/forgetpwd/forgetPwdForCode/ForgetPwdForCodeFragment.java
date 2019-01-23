package com.kehua.energy.monitor.app.business.forgetpwd.forgetPwdForCode;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.KeyboardUtils;
import com.flyco.roundview.RoundTextView;
import com.hwangjr.rxbus.RxBus;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.route.RouterMgr;

import butterknife.BindView;
import butterknife.OnClick;
import me.walten.fastgo.base.fragment.MVPFragment;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;

@Route(path = RouterMgr.FORGET_PWD_FOR_CODE)
public class ForgetPwdForCodeFragment extends MVPFragment<ForgetPwdForCodePresenter> implements ForgetPwdForCodeContract.View {

    private static final String ZERO_WIDTH_SPACE = "\uFEFF";

    @BindView(R.id.root_view)
    View mRootView;

    @BindView(R.id.view_input_blank)
    View mViewBlank;

    @BindView(R.id.rl_center_input)
    View mViewCenterInput;

    @BindView(R.id.tv_tip)
    TextView mTvTip;

    @BindView(R.id.et_code_1)
    EditText mEtCode1;

    @BindView(R.id.et_code_2)
    EditText mEtCode2;

    @BindView(R.id.et_code_3)
    EditText mEtCode3;

    @BindView(R.id.et_code_4)
    EditText mEtCode4;

    @BindView(R.id.tv_vercode)
    RoundTextView mRtvVercode;

    public ForgetPwdForCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_forget_pwd_for_code;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        RxBus.get().register(this);

        mRootView.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                mRootView.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);

                int targetWidth = (int) ((mViewCenterInput.getWidth() - mEtCode1.getWidth() * 4) * 1.0 / 3);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(targetWidth, mViewBlank.getLayoutParams().height);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                mViewBlank.setLayoutParams(layoutParams);

            }
        });

        addEdittextSelection(mEtCode1, mEtCode2, null);
        addEdittextSelection(mEtCode2, mEtCode3, mEtCode1);
        addEdittextSelection(mEtCode3, mEtCode4, mEtCode2);
        addEdittextSelection(mEtCode4, null, mEtCode3);

        mEtCode1.requestFocus();
        KeyboardUtils.hideSoftInput(mRootView);
    }

    private void addEdittextSelection(final EditText editText, final EditText nextEdittext, final EditText preEdittext) {

        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    EditText et = (EditText) v;
                    if (et.getText().length() < 2) {
                        editText.setText(ZERO_WIDTH_SPACE);
                    }
                    et.setSelection(et.getText().length());//将光标移至文字末尾
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            int oriLenght = 1;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oriLenght = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //填写动作
                if (oriLenght < 2 && s.length() > 1 && nextEdittext != null) {
                    nextEdittext.requestFocus();
                } else if (oriLenght < 2 && s.length() > 1 && nextEdittext == null) {//如果是最后一栏输入完成，则进行验证码验证

                    StringBuilder stringBuilder = new StringBuilder(mEtCode1.getText().toString().trim());
                    stringBuilder.append(mEtCode2.getText().toString().trim());
                    stringBuilder.append(mEtCode3.getText().toString().trim());
                    stringBuilder.append(mEtCode4.getText().toString().trim());

                    clearAllInput();
                    mPresenter.verCode(stringBuilder.toString());
                } else if (s.length() == 0) {
                    if (preEdittext != null) {
                        preEdittext.requestFocus();
                    } else {
                        editText.setText(ZERO_WIDTH_SPACE);
                    }
                }

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
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

    @OnClick(R.id.tv_vercode)
    void requestVercode() {
        mPresenter.loadVerCode();
    }

    @Override
    public void clearAllInput() {
        mEtCode1.setText(ZERO_WIDTH_SPACE);
        mEtCode2.setText(ZERO_WIDTH_SPACE);
        mEtCode3.setText(ZERO_WIDTH_SPACE);
        mEtCode4.setText(ZERO_WIDTH_SPACE);

        mEtCode1.requestFocus();
    }

    @Override
    public void requestVerCodeOnClickAble(boolean clickAble) {
        int colorId = clickAble ? R.color.btn_blue_nor : R.color.text_gray;
        mRtvVercode.setClickable(clickAble);
        mRtvVercode.setTextColor(ContextCompat.getColor(Fastgo.getContext(), colorId));
        mRtvVercode.getDelegate().setStrokeColor(ContextCompat.getColor(Fastgo.getContext(), colorId));
    }

    @Override
    public void updateRequestCodeText(String text) {
        mRtvVercode.setText(text);
    }
}


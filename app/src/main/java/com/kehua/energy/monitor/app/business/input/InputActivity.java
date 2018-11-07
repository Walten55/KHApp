package com.kehua.energy.monitor.app.business.input;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XSimpleActivity;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;
import me.walten.fastgo.widget.XEditText;
import me.walten.fastgo.widget.titlebar.XTitleBar;


public class InputActivity extends XSimpleActivity {

    @BindView(R.id.edit_text)
    XEditText mMsgEt;

    @BindView(R.id.tv_unit)
    TextView mUnitTv;

    private static InputConfig mConfig;

    private static String mTitle;

    private int digits = 0;

    public static void openInput(Activity aty, String title, InputConfig config) {
        mTitle = title;
        mConfig = config;
        if (mConfig == null)
            throw new NullPointerException("InputConfig is null");
        aty.startActivity(new Intent(aty, InputActivity.class));
    }

    public static void openInput(Activity aty, InputConfig config) {
        openInput(aty, null, config);
    }


    @Override
    protected boolean enableImmersive(ImmersionBar immersionBar) {
        immersionBar.statusBarDarkFont(true);
        immersionBar.keyboardEnable(true);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mConfig = null;
    }


    @Override
    public int getLayoutResId() {
        return R.layout.activity_input;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        setFullScreen();
        cancelFullScreen();

        mTitle = mConfig.getTitle();
        mTitleBar.getCenterTextView().setText(mTitle == null ? getString(R.string.输入) : mTitle);
        mTitleBar.setListener(new XTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {

                if (action == XTitleBar.ACTION_RIGHT_TEXT) {
                    if (StringUtils.isEmpty(mConfig.check(mMsgEt.getText().toString()))) {
                        if (digits > 0) {
                            if (!mMsgEt.getText().toString().contains(".")) {
                                //输入不包含.  例如 输入1 精度为2 则结果是100
                                String newResult = mMsgEt.getText().toString();
                                for (int i = 0; i < digits; i++) {
                                    newResult += 0;
                                }
                                mConfig.onResult(Integer.valueOf(newResult) + "");
                                finish();
                            } else {
                                String[] split = mMsgEt.getText().toString().split("\\.");
                                if (split != null && split.length > 1 && split[1].length() < digits) {
                                    String newResult = mMsgEt.getText().toString().replace(".", "");
                                    for (int i = 0; i < digits - split[1].length(); i++) {
                                        newResult += 0;
                                    }
                                    mConfig.onResult(Integer.valueOf(newResult) + "");
                                    finish();
                                } else {
                                    mConfig.onResult(mMsgEt.getText().toString().replace(".", ""));
                                    finish();
                                }
                            }

                        } else {
                            mConfig.onResult(mMsgEt.getText().toString());
                            finish();
                        }
                    } else {
                        XToast.error(mConfig.check(mMsgEt.getText().toString()));
                    }
                } else if (action == XTitleBar.ACTION_LEFT_BUTTON) {
                    finish();
                }

            }
        });

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        //mMsgEt.setHint(mConfig.getHintMsg());
        mUnitTv.setText(mConfig.getHintMsg());
        mMsgEt.setInputType(mConfig.getInputType());
        mMsgEt.setText(mConfig.getOldMsg());
        mMsgEt.setSelection(mMsgEt.getText().toString().length());
        mMsgEt.setFocusable(true);
        digits = mConfig.getDigits();
        if (digits != 0)
            mMsgEt.setFilters(new InputFilter[]{new CashierInputFilter(digits)});
        mConfig.customSetting(mMsgEt);
    }

    @Override
    public void setupComponent(@NonNull AppComponent appComponent) {
        DaggerActivityComponent.builder()
                .appComponent(appComponent)
                .activityModule(new ActivityModule(this))
                .build()
                .inject(this);
    }

    public interface InputConfig {

        void customSetting(XEditText editText);

        String getTitle();

        String getOldMsg();

        String getHintMsg();

        int getDigits();

        int getInputType();

        /**
         * @author walten
         * @time 2017/11/30  10:01
         * @describe 如果错误返回错误信息如果正确返回null或空字符即可
         */
        String check(String msg);

        void onResult(String msg);
    }

    public static class SimpleInputConfig implements InputConfig {

        @Override
        public void customSetting(XEditText editText) {

        }

        @Override
        public String getTitle() {
            return null;
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
            return InputType.TYPE_CLASS_TEXT;
        }

        @Override
        public String check(String msg) {
            return null;
        }

        @Override
        public void onResult(String msg) {

        }
    }

    class CashierInputFilter implements InputFilter {
        Pattern mPattern;

        //输入的最大金额
        private static final int MAX_VALUE = Integer.MAX_VALUE;
        //小数点后的位数
        private int POINTER_LENGTH = 2;

        private static final String POINTER = ".";

        private static final String ZERO = "0";

        public CashierInputFilter(int pointerLength) {
            POINTER_LENGTH = pointerLength;
            mPattern = Pattern.compile("([0-9]|\\.|\\-)*");
        }

        /**
         * @param source 新输入的字符串
         * @param start  新输入的字符串起始下标，一般为0
         * @param end    新输入的字符串终点下标，一般为source长度-1
         * @param dest   输入之前文本框内容
         * @param dstart 原内容起始坐标，一般为0
         * @param dend   原内容终点坐标，一般为dest长度-1
         * @return 输入内容
         */
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String sourceText = source.toString();
            String destText = dest.toString();

            //验证删除等按键
            if (TextUtils.isEmpty(sourceText)) {
                return "";
            }

            Matcher matcher = mPattern.matcher(source);

            //已经输入小数点的情况下，只能输入数字
            if (destText.contains(POINTER)) {
                if (!matcher.matches()) {
                    return "";
                } else {
                    if (POINTER.equals(source)) {  //只能输入一个小数点
                        return "";
                    }
                }

                //验证小数点精度，保证小数点后只能输入两位
                int index = destText.indexOf(POINTER);
                int length = dend - index;

                if (length > POINTER_LENGTH) {
                    return dest.subSequence(dstart, dend);
                }
            } else {
                //没有输入小数点的情况下，只能输入小数点和数字，但首位不能输入小数点和0
                if (!matcher.matches()) {
                    return "";
                } else {
                    if ((POINTER.equals(source)) && TextUtils.isEmpty(destText)) {
                        return "";
                    }
                }
            }

//            //验证输入金额的大小
//            double sumText = Double.parseDouble(destText + sourceText);
//            if (sumText > MAX_VALUE) {
//                return dest.subSequence(dstart, dend);
//            }

            return dest.subSequence(dstart, dend) + sourceText;
        }
    }
}

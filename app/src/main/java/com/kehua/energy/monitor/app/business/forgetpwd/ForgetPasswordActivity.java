package com.kehua.energy.monitor.app.business.forgetpwd;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import me.walten.fastgo.base.fragment.SimpleFragment;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.widget.XEditText;
import me.walten.fastgo.widget.titlebar.XTitleBar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.KeyboardUtils;
import com.flyco.roundview.RoundTextView;
import com.gyf.barlibrary.ImmersionBar;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.adapter.CommonViewPagerAdapter;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.kehua.energy.monitor.app.view.ViewPagerSlide;

import java.util.ArrayList;

@Route(path = RouterMgr.FORGET_PWD)
public class ForgetPasswordActivity extends XMVPActivity<ForgetPasswordPresenter> implements ForgetPasswordContract.View {

    @BindView(R.id.view_pager)
    ViewPagerSlide mViewPagerSlide;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_forget_password;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        setFullScreen();
        cancelFullScreen();
        RxBus.get().register(this);

        mTitleBar.setListener(new XTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == XTitleBar.ACTION_LEFT_BUTTON) {
                    //除了第一页返回键是结束当前活动,其他的都是返回上一个页面
                    if (mViewPagerSlide.getCurrentItem() == 0) {
                        finish();
                    } else if (mViewPagerSlide.getCurrentItem() == 1) {
                        mViewPagerSlide.setCurrentItem(0);
                    } else if (mViewPagerSlide.getCurrentItem() == 2) {
                        mViewPagerSlide.setCurrentItem(1);
                    }
                } else if (action == XTitleBar.ACTION_RIGHT_TEXT) {
                    mPresenter.loadVerCode();
                }
            }
        });

        ArrayList<String> titleDatas = new ArrayList<>();
        titleDatas.add(getString(R.string.邮箱));
        titleDatas.add(getString(R.string.验证码));
        titleDatas.add(getString(R.string.设置新密码));

        ArrayList<SimpleFragment> fragmentList = new ArrayList<>();
        fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.FORGET_PWD_FOR_EMAIL).navigation());
        fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.FORGET_PWD_FOR_CODE).navigation());
        fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.NEW_PASSWORD).navigation());

        CommonViewPagerAdapter commonViewPagerAdapter = new CommonViewPagerAdapter(getSupportFragmentManager(), titleDatas, fragmentList);
        mViewPagerSlide.setAdapter(commonViewPagerAdapter);
        mViewPagerSlide.setSlide(false);
        mViewPagerSlide.setCurrentPageCallBacker(new ViewPagerSlide.CurrentPageCallBacker() {
            @Override
            public void call(int item) {
                switch (item) {
                    case 0:
                        mTitleBar.getRightTextView().setText("");
                        requestVerCodeOnClickAble(false);
                        break;
                    case 1:
                        mTitleBar.getCenterTextView().setText(Fastgo.getContext().getString(R.string.忘记密码));
                        mTitleBar.getRightTextView().setText(Fastgo.getContext().getString(R.string.重新发送验证码));
                        mTitleBar.getRightTextView().setTextColor(ContextCompat.getColor(Fastgo.getContext(), R.color.text_blue));
                        requestVerCodeOnClickAble(true);
                        break;
                    case 2:
                        mTitleBar.getCenterTextView().setText(Fastgo.getContext().getString(R.string.设置新密码));
                        mTitleBar.getRightTextView().setText("");
                        requestVerCodeOnClickAble(false);
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
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
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarColor(R.color.white);
        return true;
    }


    @Override
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Config.EVENT_CODE_FROGET_PHONECODE)
            }
    )
    public void toCodeFragment(Object object) {
        if (mViewPagerSlide != null && mViewPagerSlide.getAdapter().getCount() > 1) {
            mViewPagerSlide.setCurrentItem(1);
        }
    }

    @Override
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Config.EVENT_CODE_NEW_PASSWORD)
            }
    )
    public void toNewPasswordFragment(Object object) {
        if (mViewPagerSlide != null && mViewPagerSlide.getAdapter().getCount() > 2) {
            mViewPagerSlide.setCurrentItem(2);
        }
    }

    @Override
    public void requestVerCodeOnClickAble(boolean clickAble) {
        int colorId = clickAble ? R.color.btn_blue_nor : R.color.text_gray;
        mTitleBar.getRightTextView().setClickable(clickAble);
        mTitleBar.getRightTextView().setTextColor(ContextCompat.getColor(this, colorId));
    }

    @Override
    public void updateRequestCodeText(String text) {
        mTitleBar.getRightTextView().setText(text);
    }

}
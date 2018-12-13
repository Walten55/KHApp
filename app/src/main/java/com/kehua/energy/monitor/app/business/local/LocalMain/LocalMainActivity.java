package com.kehua.energy.monitor.app.business.local.LocalMain;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.KeyboardUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.adapter.CommonViewPagerAdapter;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.dialog.UnlockDialogFragment;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.kehua.energy.monitor.app.view.ViewPagerSlide;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import me.walten.fastgo.base.fragment.SimpleFragment;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;

@Route(path = RouterMgr.LOCAL_MAIN)
public class LocalMainActivity extends XMVPActivity<LocalMainPresenter> implements LocalMainContract.View {

    @BindView(R.id.view_pager)
    ViewPagerSlide mViewPager;

    @BindView(R.id.bottom_bar)
    BottomBar mBottomBar;

    private UnlockDialogFragment mPwdDialog;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_local_main;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        setFullScreen();
        cancelFullScreen();
        RxBus.get().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.startCollecting();

        ArrayList<String> titleDatas = new ArrayList<>();
        titleDatas.add(getString(R.string.监控));
        titleDatas.add(getString(R.string.告警));
        if (LocalUserManager.getRole() != LocalUserManager.ROLE_NORMAL) {
            titleDatas.add(getString(R.string.历史));
        }
        titleDatas.add(getString(R.string.设置));

        ArrayList<SimpleFragment> fragmentList = new ArrayList<>();

        fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_MONITOR).navigation());
        fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_ALARM).navigation());
        if (LocalUserManager.getRole() != LocalUserManager.ROLE_NORMAL) {
            fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_HISTORY).navigation());
        }
        fragmentList.add((SimpleFragment) ARouter.getInstance().build(RouterMgr.LOCAL_SETTING).navigation());

        CommonViewPagerAdapter myViewPageAdapter = new CommonViewPagerAdapter(getSupportFragmentManager(), titleDatas, fragmentList);
        mViewPager.setAdapter(myViewPageAdapter);
        mViewPager.setSlide(false);

        if (LocalUserManager.getRole() == LocalUserManager.ROLE_NORMAL) {
            mBottomBar.setItems(R.xml.bottom_tabs_for_local_nomal);
            mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(int tabId) {
                    mViewPager.setCurrentItem(tabId == R.id.tab_monitor ? 0 :
                            tabId == R.id.tab_alarm ? 1 : 2, false);
                }
            });
        } else {
            mBottomBar.setItems(R.xml.bottom_tabs_for_local);
            mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(int tabId) {
                    mViewPager.setCurrentItem(tabId == R.id.tab_monitor ? 0 :
                            tabId == R.id.tab_alarm ? 1 : tabId == R.id.tab_history ? 2 : 3, false);
                }
            });
        }

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
        //immersionBar.statusBarDarkFont(true);
        return false;
    }

    //记录用户首次点击返回键的时间
    private long firstTime = 0;
    @Override
    public void onBackPressedSupport() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            XToast.normal(getString(R.string.再按一次退出本地模式));
            firstTime = secondTime;
        } else {
            RouterMgr.get().localLogin();
            super.onBackPressedSupport();
        }

    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Config.EVENT_CODE_COLLECT_COMPLETE)
            }
    )
    public void collectComplete(Object o) {
        DeviceData lockData = CacheManager.getInstance().get(Frame.机器锁定状态地址());

        if(lockData!=null&&lockData.getIntValue() == 1){
            //锁定
            RxBus.get().post(Config.EVENT_CODE_LOCK, "");
            locked();
        }else{
            if(mPwdDialog!=null){
                mPwdDialog.dismiss();
            }
        }
    }

    public void locked() {
        if(CacheManager.getInstance().get(Frame.机器锁定状态地址())!=null
                &&CacheManager.getInstance().get(Frame.机器锁定状态地址()).getIntValue() == 1){
            //机器锁定
            if(mPwdDialog==null){
                mPwdDialog = new UnlockDialogFragment();
                mPwdDialog.setCancelable(false);
                mPwdDialog.show(getSupportFragmentManager(), "pwdDialog",
                        CacheManager.getInstance().get(Frame.试用状态地址()).getIntValue()==0?getString(R.string.试用期密码)
                                :getString(R.string.开机密码),
                        new UnlockDialogFragment.OnEditPwdDialogFragmentListener() {
                            @Override
                            public void onSubmit(String msg) {
                                if(CacheManager.getInstance().get(Frame.试用状态地址()).getIntValue()==0){
                                    //下发试用期密码
                                    mPresenter.save(Frame.试用期密码地址[0], Frame.试用期密码地址[1], Integer.valueOf(msg.trim()), new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean success) throws Exception {
                                            if(success){
                                                mPwdDialog.dismiss();
                                                KeyboardUtils.hideSoftInput(LocalMainActivity.this);
                                                mPwdDialog = null;
                                            }
                                        }
                                    });
                                }else {
                                    //下发开机密码
                                    mPresenter.save(Frame.开机密码地址[0], Frame.开机密码地址[1], Integer.valueOf(msg.trim()), new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean success) throws Exception {
                                            if(success){
                                                mPwdDialog.dismiss();
                                                KeyboardUtils.hideSoftInput(LocalMainActivity.this);
                                                mPwdDialog = null;
                                            }
                                        }
                                    });
                                }

                            }
                        });
            }

        }
    }
}
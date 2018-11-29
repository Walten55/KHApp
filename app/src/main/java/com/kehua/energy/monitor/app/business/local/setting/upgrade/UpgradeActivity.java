package com.kehua.energy.monitor.app.business.local.setting.upgrade;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.StringUtils;
import com.codekidlabs.storagechooser.Content;
import com.codekidlabs.storagechooser.StorageChooser;
import com.flyco.roundview.RoundTextView;
import com.gyf.barlibrary.ImmersionBar;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;
import me.walten.fastgo.widget.titlebar.XTitleBar;

@Route(path = RouterMgr.LOCAL_UPGRADE)
public class UpgradeActivity extends XMVPActivity<UpgradePresenter> implements UpgradeContract.View {

    private StorageChooser mStorageChooser;

    @BindView(R.id.tv_path)
    RoundTextView mPathTv;

    @BindView(R.id.tv_status)
    TextView mStatusTv;

    @BindView(R.id.tv_submit)
    TextView mSubmitTv;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_upgrade;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

        setFullScreen();
        cancelFullScreen();

        mTitleBar.setListener(new XTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == XTitleBar.ACTION_LEFT_BUTTON) {
                    if (mSubmitTv.getVisibility() == View.GONE) {
                        XToast.error(getString(R.string.升级中请勿退出));
                    }else {
                        finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalUserManager.IN_THE_UPGRADE = false;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        LocalUserManager.IN_THE_UPGRADE = true;
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


    @OnClick(R.id.tv_choose)
    public void pickFile(View view) {
        if (mSubmitTv.getVisibility() == View.GONE)
            return;

        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            if (mStorageChooser == null) {
                                Content content = new Content();
                                content.setOverviewHeading(getString(R.string.存储卡));
                                content.setInternalStorageText(getString(R.string.内部存储卡));
                                content.setFreeSpaceText(getString(R.string.剩余));
                                mStorageChooser = new StorageChooser.Builder()
                                        .withActivity(UpgradeActivity.this)
                                        .withFragmentManager(getFragmentManager())
                                        .withMemoryBar(false)
                                        .withContent(content)
                                        .allowCustomPath(true)
                                        .setType(StorageChooser.FILE_PICKER)
                                        .build();

                                mStorageChooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                                    @Override
                                    public void onSelect(String path) {
                                        mPathTv.setText(path);
                                    }
                                });
                            }

                            mStorageChooser.show();
                        } else {
                            XToast.error(UpgradeActivity.this.getString(R.string.缺少相关权限));
                        }
                    }
                });


    }


    @OnClick(R.id.tv_submit)
    public void submit(View view) {
        if (!StringUtils.isEmpty(mPathTv.getText().toString())) {
            mPresenter.upload(mPathTv.getText().toString(), new Consumer<Boolean>() {
                @Override
                public void accept(Boolean success) throws Exception {
                    if(success){
                        mPathTv.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                stopWaiting();
                                XToast.success(getString(R.string.上传成功));
                                mSubmitTv.setVisibility(View.GONE);
                                mPresenter.startUpgrade();
                            }
                        },1000*12);

                    }else {
                        stopWaiting();
                    }
                }
            });
        } else {
            XToast.error(getString(R.string.请选择升级文件));
        }

    }

    @Override
    public void onUpgrade(String status,int statusCode) {
        mStatusTv.setVisibility(View.VISIBLE);
        mStatusTv.setText(status);

        if(statusCode==3||statusCode==4){
            mSubmitTv.setVisibility(View.VISIBLE);
        }else {
            mSubmitTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (mSubmitTv.getVisibility() == View.GONE) {
            XToast.error(getString(R.string.升级中请勿退出));
        } else {
            super.onBackPressedSupport();
        }

    }
}
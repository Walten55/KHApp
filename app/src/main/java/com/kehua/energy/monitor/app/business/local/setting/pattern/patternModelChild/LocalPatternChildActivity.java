package com.kehua.energy.monitor.app.business.local.setting.pattern.patternModelChild;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.gyf.barlibrary.ImmersionBar;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XMVPActivity;
import com.kehua.energy.monitor.app.business.input.InputActivity;
import com.kehua.energy.monitor.app.business.local.setting.advanced.AdvancedContract;
import com.kehua.energy.monitor.app.business.local.setting.advanced.AdvancedPresenter;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.model.entity.PointInfo;
import com.kehua.energy.monitor.app.model.entity.SettingEntity;
import com.kehua.energy.monitor.app.model.entity.Standard;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.kehua.energy.monitor.app.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;
import me.walten.fastgo.widget.XEditText;
import me.walten.fastgo.widget.titlebar.XTitleBar;

@Route(path = RouterMgr.LOCAL_SETTING_PATTERN_CHILD)
public class LocalPatternChildActivity extends XMVPActivity<LocalPatternChildPresenter> implements LocalPatternChildContract.View, BaseQuickAdapter.OnItemClickListener {

    @Autowired
    String sGroup;

    PatternChildAdapter mAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Inject
    AdvancedPresenter mAdvancedPresenter;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_local_pattern_child;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        setFullScreen();
        cancelFullScreen();
        mTitleBar.setListener(new XTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == XTitleBar.ACTION_LEFT_BUTTON) {
                    finish();
                }
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if (!mPresenter.dealData(sGroup)) {
            finish();
        }
        mTitleBar.getCenterTextView().setText(sGroup);
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

    @Override
    public void setData(List<PointInfo> data) {
        if (mAdapter == null) {
            mAdapter = new PatternChildAdapter(data);
            mAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
        }

        mAdapter.setNewData(data);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.get().register(this);
        mAdvancedPresenter.attachView(new AdvancedContract.View() {
            @Override
            public void onSetupData(List<SettingEntity> data) {
                //do nothing
            }

            @Override
            public void onUpdateData(Object o) {
                LocalPatternChildActivity.this.onUpdateData(o);
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
                LocalPatternChildActivity.this.showTipDialog(opsStatus, msg);
            }

            @Override
            public void showTipDialog(String msg) {
                LocalPatternChildActivity.this.showTipDialog(msg);
            }

            @Override
            public void startWaiting(String msg) {
                LocalPatternChildActivity.this.startWaiting(msg);
            }

            @Override
            public void stopWaiting() {
                LocalPatternChildActivity.this.stopWaiting();
            }

            @Override
            public void showToast(int opsStatus, String msg) {
                LocalPatternChildActivity.this.showToast(opsStatus, msg);
            }

            @Override
            public void showToast(String msg) {
                LocalPatternChildActivity.this.showToast(msg);
            }

            @Override
            public void finishView() {
                LocalPatternChildActivity.this.finishView();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onUpdateData(null);
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
        final PointInfo pointInfo = mAdapter.getItem(position);

        int adress = Integer.parseInt(pointInfo.getAddress().trim());
        final DeviceData deviceData = CacheManager.getInstance().get(adress);

        InputActivity.openInput(LocalPatternChildActivity.this, new InputActivity.InputConfig() {
            @Override
            public void customSetting(XEditText editText) {

            }

            @Override
            public String getTitle() {
                return deviceData != null ? deviceData.getDescription() : null;
            }

            @Override
            public String getOldMsg() {
                return deviceData != null ? deviceData.getParseValue() : null;
            }

            @Override
            public String getHintMsg() {
                return null;
            }

            @Override
            public int getDigits() {
                if (pointInfo.getAccuracy() > -1) {
                    return pointInfo.getAccuracy();
                } else
                    return 0;
            }

            @Override
            public int getInputType() {
                if (deviceData != null && "string".equals(pointInfo.getDataType())) {
                    return InputType.TYPE_CLASS_TEXT;
                } else if (deviceData != null && ("double".equals(pointInfo.getDataType()))) {
                    return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
                }else if(deviceData != null && "double_signed".equals(pointInfo.getDataType())){
                    return InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL ;
                }else if(deviceData != null && "int_signed".equals(pointInfo.getDataType())){
                    return InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER ;
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
                    if (!"string".equals(pointInfo.getDataType())) {
                        mAdvancedPresenter.save(Integer.valueOf(pointInfo.getAddress()), Integer.valueOf(msg.trim()), new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean success) throws Exception {
                                if (deviceData != null && success) {
                                    deviceData.setParseValue(Utils.parseAccuracy(Integer.valueOf(msg), deviceData.getAccuracy()));
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    } else {
                        mAdvancedPresenter.save(Integer.valueOf(pointInfo.getAddress()), Integer.valueOf(pointInfo.getAddress()) + pointInfo.getByteCount() / 2 - 1, msg, new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean success) throws Exception {
                                if (deviceData != null && success) {
                                    deviceData.setParseValue(msg);
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

    @OnClick(R.id.tv_submit)
    void submit() {
        finish();
    }
}
package com.kehua.energy.monitor.app.business.local.setting.pattern;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
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
import com.kehua.energy.monitor.app.business.local.setting.advanced.AdvancedContract;
import com.kehua.energy.monitor.app.business.local.setting.advanced.AdvancedPresenter;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.model.entity.PatternEntity;
import com.kehua.energy.monitor.app.model.entity.PatternHead;
import com.kehua.energy.monitor.app.model.entity.PointInfo;
import com.kehua.energy.monitor.app.model.entity.SettingEntity;
import com.kehua.energy.monitor.app.model.entity.Standard;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;
import me.walten.fastgo.widget.XEditText;

@Route(path = RouterMgr.LOCAL_SETTING_PATTERN)
public class PatternFragment extends XMVPFragment<PatternPresenter> implements PatternContract.View, BaseQuickAdapter.OnItemClickListener, OnRefreshListener {

    private PatternAdapter mAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    @Inject
    AdvancedPresenter mAdvancedPresenter;

    WeakReference<Context> localContext = new WeakReference<Context>(ActivityUtils.getTopActivity() == null ? Fastgo.getContext() : ActivityUtils.getTopActivity());

    public PatternFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_pattern;
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
    public void onSetupData(List<MultiItemEntity> data) {
        if (mAdapter == null) {
            mAdapter = new PatternAdapter(data, mAdvancedPresenter);
            mAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mAdapter);

            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPresenter.expandList(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }, 3000);
        }else{
            mAdapter.setNewData(data);
            mPresenter.expandList(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
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
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MultiItemEntity multiItemEntity = mAdapter.getItem(position);
        int adress = -1;

        switch (multiItemEntity.getItemType()) {
            case PatternAdapter.TYPE_HEAD_TEXT:
                PatternHead patternHead = (PatternHead) multiItemEntity;
                adress = Integer.parseInt(patternHead.getPointInfo().getAddress());

                if (CacheManager.getInstance().get(adress) == null) {
                    return;
                }
                dealHeadTextOnClick((PatternHead) multiItemEntity, position);
                break;
            case PatternAdapter.TYPE_HEAD_SWITCH:
                PatternHead patternHeadSW = (PatternHead) multiItemEntity;
                adress = Integer.parseInt(patternHeadSW.getPointInfo().getAddress());
                DeviceData deviceData = CacheManager.getInstance().get(adress);

                if (deviceData != null && deviceData.getIntValue() != Frame.OFF
                        && adress != Frame.L_HFRT模式地址) {
                    RouterMgr.get().localSettingPatternChild(patternHeadSW.getPointInfo().getDescriptionCN());
                }
                break;
            case PatternAdapter.TYPE_CONTENT_TEXT:
                PatternEntity patternEntity = (PatternEntity) multiItemEntity;
                adress = Integer.parseInt(patternEntity.getPointInfo().getAddress());

                if (CacheManager.getInstance().get(adress) == null) {
                    return;
                }
                dealContentTextOnClick((PatternEntity) multiItemEntity, position);
                break;
            case PatternAdapter.TYPE_CONTENT_LINECHART:
                PatternEntity patternEntityLineChart = (PatternEntity) multiItemEntity;
                adress = Integer.parseInt(patternEntityLineChart.getData()[0].get(0).getAddress());

                if (CacheManager.getInstance().get(adress) == null) {
                    return;
                }
                RouterMgr.get().localSettingPatternChild(patternEntityLineChart.getData()[0].get(0).getSgroup());
                break;
        }
    }

    private void dealContentTextOnClick(final PatternEntity patternEntity, int adapterPos) {

        final PointInfo pointInfo = patternEntity.getPointInfo();

        int adress = Integer.parseInt(pointInfo.getAddress().trim());
        final DeviceData deviceData = CacheManager.getInstance().get(adress);

        InputActivity.openInput(getActivity(), new InputActivity.InputConfig() {
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
                } else if (deviceData != null && ("double".equals(pointInfo.getDataType()) || "double_signed".equals(pointInfo.getDataType()))) {
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
                    if (!"string".equals(pointInfo.getDataType())) {
                        mAdvancedPresenter.save(Integer.valueOf(pointInfo.getAddress()), Integer.valueOf(msg.replace(".", "").trim()), new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean success) throws Exception {
                                if (deviceData != null && success) {
                                    deviceData.setParseValue(msg);
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

    private void dealHeadTextOnClick(final PatternHead patternHead, final int adapterPos) {
        DeviceData deviceData = CacheManager.getInstance().get(Integer.valueOf(patternHead.getPointInfo().getAddress().trim()));
        if (deviceData == null) {
            return;
        }

        final String[] stringItems;
        if (adapterPos == 0) {
            stringItems = new String[]{
                    localContext.get().getString(R.string.关闭),
                    localContext.get().getString(R.string.无功支撑模式),
                    localContext.get().getString(R.string.零无功模式),
            };
        } else {
            stringItems = new String[]{
                    localContext.get().getString(R.string.关闭),
                    localContext.get().getString(R.string.线性),
                    localContext.get().getString(R.string.滞回),
            };
        }

        final ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems, null);
        dialog.isTitleShow(false).show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mAdvancedPresenter.save(Integer.valueOf(patternHead.getPointInfo().getAddress().trim()), position, new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        DeviceData deviceData = CacheManager.getInstance().get(Integer.valueOf(patternHead.getPointInfo().getAddress().trim()));
                        if (success && deviceData != null) {
                            deviceData.setIntValue(position);
                            if (position == 0) {
                                mAdapter.collapse(adapterPos, true);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                mAdapter.expand(adapterPos, true);
                                mAdapter.notifyDataSetChanged();

                                int adress = Integer.parseInt(patternHead.getPointInfo().getAddress());
                                //如果不是L_HVRT模式地址,跳转
                                if (adress != Frame.L_HVRT模式地址) {
                                    RouterMgr.get().localSettingPatternChild(patternHead.getPointInfo().getDescriptionCN());
                                }
                            }
                        }

                    }
                });
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        onRefresh(mRefreshLayout);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mPresenter.setupData();
        refreshLayout.finishRefresh(1000);
    }

    @Override
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Config.EVENT_CODE_COLLECT_SETTING_COMPLETE)
            }
    )
    public void onUpdateData(Object o) {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
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
                PatternFragment.this.onUpdateData(o);
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
                PatternFragment.this.showTipDialog(opsStatus, msg);
            }

            @Override
            public void showTipDialog(String msg) {
                PatternFragment.this.showTipDialog(msg);
            }

            @Override
            public void startWaiting(String msg) {
                PatternFragment.this.startWaiting(msg);
            }

            @Override
            public void stopWaiting() {
                PatternFragment.this.stopWaiting();
            }

            @Override
            public void showToast(int opsStatus, String msg) {
                PatternFragment.this.showToast(opsStatus, msg);
            }

            @Override
            public void showToast(String msg) {
                PatternFragment.this.showToast(msg);
            }

            @Override
            public void finishView() {
                PatternFragment.this.finishView();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.get().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.get().unregister(this);
        mAdvancedPresenter.detachView();
    }
}


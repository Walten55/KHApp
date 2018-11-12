package com.kehua.energy.monitor.app.business.local.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.base.XMVPFragment;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.route.RouterMgr;

import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;

@Route(path = RouterMgr.LOCAL_HISTORY)
public class HistoryFragment extends XMVPFragment<HistoryPresenter> implements HistoryContract.View {

    private int template = -1;
    private int recordType = Frame.并脱网记录;
    private int registerAdr = 0;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_history;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

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

//    @Override
//    public void onSupportVisible() {
//        super.onSupportVisible();
//        RxBus.get().register(this);
//        mPresenter.recordCount(null);
//    }
//
//    @Override
//    public void onSupportInvisible() {
//        super.onSupportInvisible();
//        RxBus.get().unregister(this);
//    }
//
//    /**
//     * 本地采集成功后，接受其发送信号进行数据刷新
//     */
//    @Subscribe(
//            thread = EventThread.MAIN_THREAD,
//            tags = {
//                    @Tag(Config.EVENT_CODE_COLLECT_COMPLETE)
//            }
//    )
//    public void poll(Object o) {
//        mPresenter.recordCount(null);
//    }

    @OnClick({R.id.item_1, R.id.item_2, R.id.item_3, R.id.item_4, R.id.item_5})
    public void onClickItem(final View view) {
        mPresenter.recordCount(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean success) throws Exception {
                if(success){
                    switch (view.getId()) {
                        case R.id.item_1:
                            registerAdr = 5700;
                            recordType = Frame.并脱网记录;
                            break;
                        case R.id.item_2:
                            registerAdr = 5701;
                            recordType = Frame.历史故障;
                            break;
                        case R.id.item_3:
                            registerAdr = 5702;
                            recordType = Frame.用户日志;
                            break;
                        case R.id.item_4:
                            registerAdr = 5703;
                            recordType = Frame.功率调度;
                            break;
                    }

                    if (CacheManager.getInstance().get(registerAdr) == null
                            || (CacheManager.getInstance().get(registerAdr) != null && CacheManager.getInstance().get(registerAdr).getIntValue() == 0)) {
                        XToast.normal(getString(R.string.无记录));
                    } else {
                        if(LocalUserManager.getPn() == Frame.单相协议){
                            template = registerAdr==5700||registerAdr==5701?0:1;

                            RouterMgr.get().localHistoryInfo(CacheManager.getInstance().get(registerAdr).getIntValue(),
                                    0, recordType, template);

                        }else {

                            if(registerAdr==5702||registerAdr==5703){
                                //三相用户日志 功率调度
                                template = 4;

                                RouterMgr.get().localHistoryInfo(CacheManager.getInstance().get(registerAdr).getIntValue(),
                                        0, recordType, template);
                            }else {

                                final String[] stringItems = {
//                                    Fastgo.getContext().getString(R.string.单相并脱网历史故障记录映射模板),
//                                    Fastgo.getContext().getString(R.string.单相用户日志功率调度日志映射模板),
                                        Fastgo.getContext().getString(R.string.三相无屏并脱网历史故障记录映射模板),
                                        Fastgo.getContext().getString(R.string.三相有屏并脱网历史故障记录映射模板)
//                                    , Fastgo.getContext().getString(R.string.三相用户日志功率调度日志映射模板)
                                };
                                final ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems, null);
                                dialog.isTitleShow(true)
                                        .title(getString(R.string.选择映射模板))
                                        .show();

                                dialog.setOnOperItemClickL(new OnOperItemClickL() {
                                    @Override
                                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        template = position==0?2:3;

                                        RouterMgr.get().localHistoryInfo(CacheManager.getInstance().get(registerAdr).getIntValue(),
                                                0, recordType, template);

                                        dialog.dismiss();

                                    }
                                });

                            }
                        }
                    }
                }else {
                    XToast.error(getString(R.string.记录条数读取失败));
                }
            }
        });



    }
}


package com.kehua.energy.monitor.app.business.local.setting.basic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.base.XMVPFragment;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.di.component.DaggerFragmentComponent;
import com.kehua.energy.monitor.app.di.module.FragmentModule;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.kehua.energy.monitor.app.utils.TimeSlotUtils;
import com.kyleduo.switchbutton.SwitchButton;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XToast;

@Route(path = RouterMgr.LOCAL_SETTING_BASIC)
public class BasicFragment extends XMVPFragment<BasicPresenter> implements BasicContract.View, View.OnClickListener, OnRefreshListener, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.sb_power)
    SwitchButton mPowerSwitchButton;

    @BindView(R.id.rl_work_pattern)
    View mWorkPatternContainer;

    @BindView(R.id.ll_time_frame)
    View mTimeFrameContainer;

    @BindView(R.id.tv_work_pattern)
    TextView mWorkPatternTextView;

    @BindView(R.id.tv_reading_work_pattern)
    TextView mReadingWorkPatternView;

    @BindView(R.id.tv_reading_power)
    TextView mReadingPower;

    @BindView(R.id.ll_charge_time_container)
    LinearLayout mChargeTimeContainer;

    @BindView(R.id.ll_discharge_time_container)
    LinearLayout mDischargeTimeContainer;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    private TimePickerView pvTime;

    private TimePickerView mCommonTimePicker;


    public BasicFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_basic;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        if (LocalUserManager.getPn() == Frame.单相协议 && Frame.isStorageDevice(LocalUserManager.getDeviceType())) {
            mWorkPatternContainer.setVisibility(View.VISIBLE);
        }
        mRefreshLayout.setOnRefreshListener(this);

        mPowerSwitchButton.setOnCheckedChangeListener(this);
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

    @OnClick(R.id.rl_work_pattern)
    @Override
    public void onClickWorkPattern(View view) {
        final String[] stringItems = {
                Fastgo.getContext().getString(R.string.自用优先),
                Fastgo.getContext().getString(R.string.储能优先),
                Fastgo.getContext().getString(R.string.削峰填谷),
                Fastgo.getContext().getString(R.string.能量调度)};
        final ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems, null);
        dialog.isTitleShow(false).show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String oldText = mWorkPatternTextView.getText().toString();
                final int oldVisibility = mTimeFrameContainer.getVisibility();
                mPresenter.setWorkPattern(position, new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if(!success){
                            mWorkPatternTextView.setText(oldText);
                            mTimeFrameContainer.setVisibility(oldVisibility);
                        }
                    }
                });
                mWorkPatternTextView.setText(stringItems[position]);

                if(position == 2||position == 3){
                    mTimeFrameContainer.setVisibility(View.VISIBLE);
                }else{
                    mTimeFrameContainer.setVisibility(View.GONE);
                }
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.rl_system_time)
    @Override
    public void onClickSystemTimeSetting(View view) {
        Calendar selectedDate = Calendar.getInstance();

        if (pvTime == null) {
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();

            //正确设置方式 原因：注意事项有说明
            startDate.set(selectedDate.get(Calendar.YEAR) - 1, 0, 1);
            endDate.set(selectedDate.get(Calendar.YEAR) + 1, 11, 31);

            pvTime = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {//选中事件回调
                    mPresenter.setSystemTime(date,null);
                }
            })
                    .setType(new boolean[]{true, true, true, true, true, true})// 默认全部显示
                    .setCancelText(getString(R.string.取消))//取消按钮文字
                    .setSubmitText(getString(R.string.确定))//确认按钮文字
                    .setTitleSize(20)//标题文字大小
                    .setTitleText(getString(R.string.时间))//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(true)//是否循环滚动
                    .setTitleColor(ContextCompat.getColor(getContext(), R.color.text_black))//标题文字颜色
                    .setSubmitColor(ContextCompat.getColor(getContext(), R.color.text_blue))//确定按钮文字颜色
                    .setCancelColor(ContextCompat.getColor(getContext(), R.color.text_gray))//取消按钮文字颜色
                    .setTitleBgColor(0XFFFFFFFF)//标题背景颜色 Night mode
                    .setBgColor(0XFFFFFFFF)//滚轮背景颜色 Night mode
                    .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                    .setRangDate(startDate, endDate)//起始终止年月日设定
                    .setLabel(getString(R.string.年), getString(R.string.月), getString(R.string.日), getString(R.string.时), getString(R.string.分), getString(R.string.秒))//默认设置为年月日时分秒
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .isDialog(true)//是否显示为对话框样式
                    .build();
        }

        pvTime.setDate(selectedDate);
        pvTime.show();

    }

    private void showTimePicker(final TextView view) {
        Calendar selectedDate = Calendar.getInstance();
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            Date parse = format.parse(view.getText().toString());
            selectedDate.setTime(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        //正确设置方式 原因：注意事项有说明
        startDate.set(selectedDate.get(Calendar.YEAR) - 1, 0, 1);
        endDate.set(selectedDate.get(Calendar.YEAR) + 1, 11, 31);

        mCommonTimePicker = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                if (view.getId() == R.id.tv_start_time) {
                    TextView endTimeTv = (TextView) view.getTag();
                    try {
                        if (format.parse(endTimeTv.getText().toString()).getTime() < format.parse(format.format(date)).getTime()) {
                            XToast.error(getString(R.string.结束时间必须大于开始时间));
                            return;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (view.getId() == R.id.tv_end_time) {
                    TextView startTimeTv = (TextView) view.getTag();
                    try {
                        if (format.parse(startTimeTv.getText().toString()).getTime() > format.parse(format.format(date)).getTime()) {
                            XToast.error(getString(R.string.结束时间必须大于开始时间));
                            return;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


                view.setText(format.format(date));
            }
        })
                .setType(new boolean[]{false, false, false, true, true, false})// 默认全部显示
                .setCancelText(getString(R.string.取消))//取消按钮文字
                .setSubmitText(getString(R.string.确定))//确认按钮文字
                .setTitleSize(20)//标题文字大小
                .setTitleText(getString(R.string.时间))//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(ContextCompat.getColor(getContext(), R.color.text_black))//标题文字颜色
                .setSubmitColor(ContextCompat.getColor(getContext(), R.color.text_blue))//确定按钮文字颜色
                .setCancelColor(ContextCompat.getColor(getContext(), R.color.text_gray))//取消按钮文字颜色
                .setTitleBgColor(0XFFFFFFFF)//标题背景颜色 Night mode
                .setBgColor(0XFFFFFFFF)//滚轮背景颜色 Night mode
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel(getString(R.string.年), getString(R.string.月), getString(R.string.日), getString(R.string.时), getString(R.string.分), getString(R.string.秒))//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(true)//是否显示为对话框样式
                .build();

        mCommonTimePicker.setDate(selectedDate);
        mCommonTimePicker.show();
    }


    @OnClick(R.id.tv_add_charge_time)
    @Override
    public void onClickAddChargeTime(View view) {
        addTimeFrame(mChargeTimeContainer);
    }

    @OnClick(R.id.tv_add_discharge_time)
    @Override
    public void onClickAddDischargeTime(View view) {
        addTimeFrame(mDischargeTimeContainer);
    }

    @OnClick(R.id.tv_time_frame_submit)
    @Override
    public void onClickSubmitTimeFrame(View view) {
        List<String> listSlot = new ArrayList<>();
        List<String> listCharge = getTimeFrameList(mChargeTimeContainer,listSlot);
        List<String> listDischarge = getTimeFrameList(mDischargeTimeContainer,listSlot);

        if(TimeSlotUtils.checkOverlap(listSlot)){
            XToast.error(getString(R.string.时段重叠));
            return;
        }
        mPresenter.setTimeFrame(listCharge, listDischarge,null);
    }

    @Override
    public List<String> getTimeFrameList(LinearLayout container,List<String> slotList) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < container.getChildCount(); i++) {
            TextView startTimeTv = container.getChildAt(i).findViewById(R.id.tv_start_time);
            TextView endTimeTv = container.getChildAt(i).findViewById(R.id.tv_end_time);
            list.add(startTimeTv.getText().toString());
            list.add(endTimeTv.getText().toString());
            slotList.add(startTimeTv.getText().toString()+"-"+endTimeTv.getText().toString());
        }

        return list;
    }

    @Override
    public void addTimeFrame(LinearLayout container) {
        if (container.getChildCount() == 6) {
            XToast.error(getString(R.string.最多设置6个时段));
        } else {
            SwipeMenuLayout child = (SwipeMenuLayout) LayoutInflater.from(Fastgo.getContext()).inflate(R.layout.time_frame, null);
            child.getChildAt(1).setOnClickListener(this);
            TextView nameTv = child.findViewById(R.id.tv_name);
            nameTv.setText(getString(R.string.时段) + (container.getChildCount() + 1));

            TextView startTimeTv = child.findViewById(R.id.tv_start_time);
            TextView endTimeTv = child.findViewById(R.id.tv_end_time);

            startTimeTv.setTag(endTimeTv);
            endTimeTv.setTag(startTimeTv);

            startTimeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimePicker((TextView) v);
                }
            });
            endTimeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimePicker((TextView) v);
                }
            });

            container.addView(child);
        }
    }

    @Override
    public void onClickDelete(View view) {
        ViewGroup parent = (ViewGroup) view.getParent().getParent();
        if (parent.getId() == R.id.ll_charge_time_container) {
            mChargeTimeContainer.removeView((View) view.getParent());
            initTimeFrameView(mChargeTimeContainer);
        } else {
            mDischargeTimeContainer.removeView((View) view.getParent());
            initTimeFrameView(mDischargeTimeContainer);
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        RxBus.get().register(this);
        onUpdateData(null);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
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

        if (CacheManager.getInstance().get(Frame.开关机地址) != null) {
            mPowerSwitchButton.setCheckedImmediatelyNoEvent(CacheManager.getInstance().get(Frame.开关机地址).getIntValue() != Frame.OFF);
            CacheManager.getInstance().remove(Frame.开关机地址);

            mReadingPower.setVisibility(View.GONE);
            mPowerSwitchButton.setVisibility(View.VISIBLE);
        }

        if (CacheManager.getInstance().get(Frame.工作模式地址) != null) {
            mWorkPatternTextView.setText(Frame.getWorkPatternName(CacheManager.getInstance().get(Frame.工作模式地址).getIntValue()));

            if (CacheManager.getInstance().get(Frame.工作模式地址).getIntValue() == Frame.自用优先
                    || CacheManager.getInstance().get(Frame.工作模式地址).getIntValue() == Frame.储能优先) {
                //mTimeFrameContainer.setVisibility(View.GONE);
            } else {
                if(mWorkPatternContainer.getVisibility() == View.VISIBLE)
                    mTimeFrameContainer.setVisibility(View.VISIBLE);
                CacheManager.getInstance().remove(Frame.工作模式地址);
            }

            mReadingWorkPatternView.setVisibility(View.GONE);
        }

        if (CacheManager.getInstance().get(Frame.充电时段数地址) != null) {
            initTimeFrameView(CacheManager.getInstance().get(Frame.充电时段数地址).getIntValue(), mChargeTimeContainer);
            CacheManager.getInstance().remove(Frame.充电时段数地址);
        }

        if (CacheManager.getInstance().get(Frame.放电时段数地址) != null) {
            initTimeFrameView(CacheManager.getInstance().get(Frame.放电时段数地址).getIntValue(), mDischargeTimeContainer);
            CacheManager.getInstance().remove(Frame.放电时段数地址);
        }

    }

    @Override
    public void initTimeFrameView(int count, LinearLayout container) {
        container.removeAllViews();

        int start = container.getId() == R.id.ll_charge_time_container ? 6027 : 6040;
        for (int i = 0; i < count; i++) {
            if(i>5)
                return;

            SwipeMenuLayout child = (SwipeMenuLayout) LayoutInflater.from(Fastgo.getContext()).inflate(R.layout.time_frame, null);
            child.getChildAt(1).setOnClickListener(this);
            TextView nameTv = child.findViewById(R.id.tv_name);
            TextView startTimeTv = child.findViewById(R.id.tv_start_time);
            TextView endTimeTv = child.findViewById(R.id.tv_end_time);

            DeviceData startTimeDeviceData = CacheManager.getInstance().get(start++);
            if(startTimeDeviceData==null)
                return;
            DeviceData endTimeDeviceData = CacheManager.getInstance().get(start++);
            if(endTimeDeviceData==null)
                return;

            nameTv.setText(getString(R.string.时段) + (i + 1));
            startTimeTv.setText(startTimeDeviceData.getParseValue());
            endTimeTv.setText("24:00".equals(endTimeDeviceData.getParseValue())?"23:59":endTimeDeviceData.getParseValue());


            startTimeTv.setTag(endTimeTv);
            endTimeTv.setTag(startTimeTv);

            startTimeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimePicker((TextView) v);
                }
            });
            endTimeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimePicker((TextView) v);
                }
            });

            container.addView(child);
        }
    }

    @Override
    public void initTimeFrameView(LinearLayout container) {
        for (int i = 0; i < container.getChildCount(); i++) {
            TextView nameTv = container.getChildAt(i).findViewById(R.id.tv_name);
            nameTv.setText(getString(R.string.时段) + (i + 1));
        }
    }

    @Override
    public void onClick(View v) {
        onClickDelete(v);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mPresenter.setupData();
        refreshLayout.finishRefresh(1000);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView,final boolean isChecked) {

        final NormalDialog dialog = new NormalDialog(getActivity());
        dialog.content(Fastgo.getContext().getString(R.string.是否更改设置)).title(Fastgo.getContext().getString(R.string.温馨提示))
                .style(NormalDialog.STYLE_TWO)//
                .btnNum(2)
                .titleTextSize(23);
        dialog.btnText(getString(R.string.取消), getString(R.string.确定));
        dialog.titleTextColor(ContextCompat.getColor(Fastgo.getContext(), R.color.colorPrimary));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                mPowerSwitchButton.setCheckedImmediatelyNoEvent(!isChecked);
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                mPresenter.power(isChecked,new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if(!success)
                            mPowerSwitchButton.setCheckedImmediatelyNoEvent(!isChecked);
                    }
                });
            }
        });
        dialog.show();


    }
}


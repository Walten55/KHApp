package com.kehua.energy.monitor.app.business.local.setting.pattern;

import android.widget.CompoundButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.github.mikephil.charting.charts.LineChart;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.business.local.setting.advanced.AdvancedPresenter;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.model.entity.PatternEntity;
import com.kehua.energy.monitor.app.model.entity.PatternHead;
import com.kehua.energy.monitor.app.model.entity.PointInfo;
import com.kehua.energy.monitor.app.utils.LineChartHelper;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class PatternAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int TYPE_HEAD_TEXT = 0;
    public static final int TYPE_HEAD_SWITCH = 1;

    public static final int TYPE_CONTENT_TEXT = 2;
    public static final int TYPE_CONTENT_LINECHART = 3;

    public static final int TYPE_CONTENT_BLANK = 4;

    private AdvancedPresenter presenter;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public PatternAdapter(List<MultiItemEntity> data, AdvancedPresenter presenter) {
        super(data);

        addItemType(TYPE_HEAD_TEXT, R.layout.item_local_setting_text);
        addItemType(TYPE_HEAD_SWITCH, R.layout.item_local_setting_switch);
        addItemType(TYPE_CONTENT_TEXT, R.layout.item_local_setting_text);
        addItemType(TYPE_CONTENT_LINECHART, R.layout.item_local_setting_linechart);
        addItemType(TYPE_CONTENT_BLANK, R.layout.item_local_setting_blank);

        this.presenter = presenter;
//        expandAll();
    }

    @Override
    protected void convert(final BaseViewHolder helper, final MultiItemEntity item) {
        final int pos = helper.getAdapterPosition();

        DeviceData targetDeviceData = null;
        switch (item.getItemType()) {
            case TYPE_HEAD_TEXT:
                final PatternHead pdTextItem = (PatternHead) item;

                helper.setText(R.id.tv_name, pdTextItem.getPointInfo().getDescription());
                int adress = Integer.valueOf(pdTextItem.getPointInfo().getAddress().trim());
                targetDeviceData = CacheManager.getInstance().get(adress);

                if (targetDeviceData != null) {
                    String valueStr = "";
                    if (adress == Frame.L_HVRT模式地址) {
                        switch (targetDeviceData.getIntValue()) {
                            case 0:
                                valueStr = helper.itemView.getContext().getString(R.string.关闭);
                                break;
                            case 1:
                                valueStr = helper.itemView.getContext().getString(R.string.无功支撑模式);
                                break;
                            case 2:
                                valueStr = helper.itemView.getContext().getString(R.string.零无功模式);
                                break;
                        }
                    } else {
                        switch (targetDeviceData.getIntValue()) {
                            case 0:
                                valueStr = helper.itemView.getContext().getString(R.string.关闭);
                                break;
                            case 1:
                                valueStr = helper.itemView.getContext().getString(R.string.线性);
                                break;
                            case 2:
                                valueStr = helper.itemView.getContext().getString(R.string.滞回);
                                break;
                        }
                    }

                    helper.setText(R.id.tv_value, valueStr);
                    helper.setGone(R.id.tv_reading, false);
                    helper.setGone(R.id.tv_value, true);
                } else {
                    helper.setGone(R.id.tv_reading, true);
                    helper.setGone(R.id.tv_value, false);
                }

                break;

            case TYPE_HEAD_SWITCH:
                final PatternHead pdSwitchItem = (PatternHead) item;
                helper.setText(R.id.tv_name, pdSwitchItem.getPointInfo().getDescription());

                final SwitchButton switchButton = helper.getView(R.id.sb_value);

                targetDeviceData = CacheManager.getInstance().get(Integer.valueOf(pdSwitchItem.getPointInfo().getAddress().trim()));

                if (targetDeviceData != null) {
                    helper.setGone(R.id.tv_reading, false);
                    helper.setGone(R.id.sb_value, true);

//                    switchButton.setCheckedImmediatelyNoEvent(targetDeviceData.getIntValue() != Frame.OFF);
                    switchButton.setCheckedImmediatelyNoEvent(targetDeviceData.getIntValue() != Frame.OFF);

                    switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                            presenter.save(Integer.valueOf(pdSwitchItem.getPointInfo().getAddress().trim())
                                    , isChecked ? 1 : 0, new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean success) throws Exception {
                                            DeviceData deviceData = CacheManager.getInstance()
                                                    .get(Integer.valueOf(pdSwitchItem.getPointInfo().getAddress().trim()));
                                            if (success && deviceData != null) {
                                                deviceData.setIntValue(isChecked ? 1 : 0);
                                                if (isChecked) {
                                                    PatternAdapter.this.expand(helper.getAdapterPosition(), true);
                                                } else {
                                                    PatternAdapter.this.collapse(helper.getAdapterPosition(), true);
                                                }

                                                //传递点击事件
                                                helper.itemView.callOnClick();
                                            }
                                            //如果没有设置成功，将结果复原成就有状态
                                            if (!success) {
                                                boolean result = switchButton.isChecked();
                                                switchButton.setCheckedImmediatelyNoEvent(!result);
                                            }
                                        }
                                    });
//                            CacheManager.getInstance().remove(Integer.valueOf(pdSwitchItem.getPointInfo().getAddress().trim()));

                        }
                    });

                } else {
                    helper.setGone(R.id.tv_reading, true);
                    helper.setGone(R.id.sb_value, false);
                }
                break;

            case TYPE_CONTENT_TEXT:
                PatternEntity peTextItem = (PatternEntity) item;

                helper.setText(R.id.tv_name, peTextItem.getPointInfo().getDescription());

                targetDeviceData = CacheManager.getInstance().get(Integer.valueOf(peTextItem.getPointInfo().getAddress().trim()));

                if (targetDeviceData != null) {
                    helper.setText(R.id.tv_value, targetDeviceData.getParseValue() + " " + peTextItem.getPointInfo().getUnit());
                    helper.setGone(R.id.tv_reading, false);
                    helper.setGone(R.id.tv_value, true);
                } else {
                    helper.setGone(R.id.tv_reading, true);
                    helper.setGone(R.id.tv_value, false);
                }

                helper.setGone(R.id.view_bottom_line, false);
                ((TextView) helper.getView(R.id.tv_name)).setTextSize(helper.itemView.getResources().getDimension(R.dimen.grid_4));
                ((TextView) helper.getView(R.id.tv_value)).setTextSize(helper.itemView.getResources().getDimension(R.dimen.grid_4));
                break;

            case TYPE_CONTENT_LINECHART:
                PatternEntity peLineChartItem = (PatternEntity) item;
                List<PointInfo>[] data = peLineChartItem.getData();

                helper.addOnClickListener(R.id.tv_linechart_setting);
                //已经对应点表对象正常且读取成功才进行数据转化与展示
                DeviceData deviceInfo = CacheManager.getInstance().get(Integer.valueOf(peLineChartItem.getData()[0].get(0).getAddress().trim()));
                if (deviceInfo != null) {
                    //单条折线
                    if (peLineChartItem.isSingleLine()) {
                        List<DeviceData> deviceDatas = new ArrayList<>();
                        for (int i = 0; i < data[0].size(); i++) {
                            targetDeviceData = CacheManager.getInstance().get(Integer.valueOf(data[0].get(i).getAddress().trim()));
                            deviceDatas.add(targetDeviceData);
                        }

                        //硬件开发那群傻逼漏了QV模式的Q2，Q3值，设计上面要求强制添加为0，点表上地址又是连续的无法添加，只能在客户端强制为0，但是有些角色不一定采集得到，故在此特殊设置
                        if (deviceDatas.size() > 5 && String.valueOf(Frame.Q_V模式V1地址).equals(deviceDatas.get(0).getRegisterAddress())) {
                            DeviceData deviceDataQ2 = new DeviceData();
                            DeviceData deviceDataQ3 = new DeviceData();

                            deviceDataQ2.setParseValue(String.valueOf(0));
                            deviceDataQ3.setParseValue(String.valueOf(0));
                            deviceDatas.add(3, deviceDataQ2);
                            deviceDatas.add(5, deviceDataQ3);
                        }
                        LineChartHelper.init(helper.itemView.getContext(), (LineChart) helper.getView(R.id.linechart)).setDeviceData(deviceDatas);
                    }
                    //多条折线
                    else {
                        List<DeviceData>[] deviceDataLists = new List[peLineChartItem.getData().length];
                        List<DeviceData> deviceDatas;
                        //折线图多组
                        for (int i = 0; i < peLineChartItem.getData().length; i++) {
                            deviceDatas = new ArrayList<>();
                            //每组
                            for (int j = 0; j < peLineChartItem.getData()[i].size(); j++) {
                                targetDeviceData = CacheManager.getInstance().get(Integer.valueOf(peLineChartItem.getData()[i].get(j).getAddress().trim()));
                                deviceDatas.add(targetDeviceData);
                            }
                            deviceDataLists[i] = deviceDatas;
                        }

                        LineChartHelper.init(helper.itemView.getContext(), (LineChart) helper.getView(R.id.linechart))
                                .setDeviceData(peLineChartItem.getLables(), peLineChartItem.getColors(), deviceDataLists);
                    }

                }
                break;
        }
    }

}

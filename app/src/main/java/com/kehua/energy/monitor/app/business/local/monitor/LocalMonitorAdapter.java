package com.kehua.energy.monitor.app.business.local.monitor;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.model.entity.MonitorEntity;
import com.kehua.energy.monitor.app.model.entity.PointInfo;
import com.kehua.energy.monitor.app.utils.EnergyFlowChartHelper;
import com.kehua.energy.monitor.app.view.EnergyFlowChart;

import java.text.DecimalFormat;
import java.util.List;

public class LocalMonitorAdapter extends BaseMultiItemQuickAdapter<MonitorEntity, BaseViewHolder> {

    private EnergyFlowChart mEnergyFlowChart;

    private DecimalFormat decimalFormat = new DecimalFormat("#0.0");

    public LocalMonitorAdapter(List data) {
        super(data);
        addItemType(MonitorEntity.OVERVIEW, R.layout.item_local_monitor_overview);
        addItemType(MonitorEntity.CENTER_TITLE, R.layout.item_local_monitor_center_title);
        addItemType(MonitorEntity.LEFT_TITLE, R.layout.item_local_monitor_left_title);
        addItemType(MonitorEntity.MARGIN, R.layout.item_local_monitor_margin);
        addItemType(MonitorEntity.SIMPLE_DATA, R.layout.item_local_monitor_simple_data);
        addItemType(MonitorEntity.TABLE_HEAD, R.layout.item_local_monitor_table_head);
        addItemType(MonitorEntity.TABLE_CONTENT, R.layout.item_local_monitor_table_content);
        addItemType(MonitorEntity.SIMPLE_DATA_WITH_ADDRESS, R.layout.item_local_monitor_simple_data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MonitorEntity item) {
        switch (helper.getItemViewType()) {
            case MonitorEntity.SIMPLE_DATA_WITH_ADDRESS:
                if(Integer.valueOf(((MonitorEntity<PointInfo>)item).getData().getAddress()) == Frame.总并网用电量地址()[0]){
                    int highValue = CacheManager.getInstance().get(Frame.总并网用电量地址()[0]).getIntValue();
                    int lowValue = CacheManager.getInstance().get(Frame.总并网用电量地址()[1]).getIntValue();
                    long resultValue = highValue * 65536 + lowValue;

                    helper.setText(R.id.tv_name, ((MonitorEntity<PointInfo>)item).getData().getDescription());
                    helper.setText(R.id.tv_value, decimalFormat.format(resultValue / Math.pow(10, 1)));
                }
            case MonitorEntity.OVERVIEW:
                mEnergyFlowChart = helper.getView(R.id.energy_flow_chart);

//                mEnergyFlowChart.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mEnergyFlowChart.reset();
//                        if (!"test".equals(mEnergyFlowChart.getTag())) {
//                            mEnergyFlowChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_OUT);
//                            mEnergyFlowChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_OUT);
//                            mEnergyFlowChart.disable(EnergyFlowChart.CHILD_BR);
//                            mEnergyFlowChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_IN);
//                            mEnergyFlowChart.setTag("test");
//                        } else {
//                            mEnergyFlowChart.setTag("null");
//                            mEnergyFlowChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_OUT);
//                            mEnergyFlowChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
//                            mEnergyFlowChart.able(EnergyFlowChart.CHILD_BR);
//                            mEnergyFlowChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_IN);
//                            mEnergyFlowChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_IN);
//                        }
//
//                    }
//                });

                EnergyFlowChartHelper.setupData(mEnergyFlowChart);

                View energyStorageInfoViews = helper.getView(R.id.ll_energy_storage_info);
                if (LocalUserManager.getDeviceType() == 0x02 || LocalUserManager.getDeviceType() == 0x0B) {
                    //储能设备 包含电池信息
                    energyStorageInfoViews.setVisibility(View.VISIBLE);


                    //电池充放电
                    if (null != CacheManager.getInstance().get(Frame.电池日充电量地址())
                            && !StringUtils.isEmpty(CacheManager.getInstance().get(Frame.电池日充电量地址()).getParseValue())) {
                        helper.setText(R.id.tv_daily_charge,
                                CacheManager.getInstance().get(Frame.电池日充电量地址()).getParseValue()
                        );
                    }
                    if (null != CacheManager.getInstance().get(Frame.电池日放电量地址())
                            && !StringUtils.isEmpty(CacheManager.getInstance().get(Frame.电池日放电量地址()).getParseValue())) {
                        helper.setText(R.id.tv_daily_discharge,
                                CacheManager.getInstance().get(Frame.电池日放电量地址()).getParseValue()
                        );
                    }

                    //负载
                    if (null != CacheManager.getInstance().get(Frame.日负载耗电量地址())
                            && !StringUtils.isEmpty(CacheManager.getInstance().get(Frame.日负载耗电量地址()).getParseValue())) {
                        helper.setText(R.id.tv_daily_load,
                                CacheManager.getInstance().get(Frame.日负载耗电量地址()).getParseValue()
                        );
                    }
                    if (null != CacheManager.getInstance().get(Frame.总负载耗电量地址()[0])) {
                        int highValue = CacheManager.getInstance().get(Frame.总负载耗电量地址()[0]).getIntValue();
                        int lowValue = CacheManager.getInstance().get(Frame.总负载耗电量地址()[1]).getIntValue();
                        long resultValue = highValue * 65536 + lowValue;
                        helper.setText(R.id.tv_total_load, decimalFormat.format(resultValue / Math.pow(10, 1)));
                    }

                } else {
                    energyStorageInfoViews.setVisibility(View.GONE);
                }

                //运行状态
                if (null != CacheManager.getInstance().get(Frame.运行状态地址)
                        && !StringUtils.isEmpty(CacheManager.getInstance().get(Frame.运行状态地址).getParseValue())) {
                    helper.setText(R.id.tv_running_state, Frame.getDeviceRunningState(Integer.valueOf(
                            CacheManager.getInstance().get(Frame.运行状态地址).getParseValue()
                    )));
                }

                //并网
                if (null != CacheManager.getInstance().get(Frame.日并网发电量地址)
                        && !StringUtils.isEmpty(CacheManager.getInstance().get(Frame.日并网发电量地址).getParseValue())) {
                    helper.setText(R.id.tv_daily_grid_generation,
                            CacheManager.getInstance().get(Frame.日并网发电量地址).getParseValue()
                    );
                }

                if (null != CacheManager.getInstance().get(Frame.总并网发电量地址()[0])) {
                    int highGridValue = CacheManager.getInstance().get(Frame.总并网发电量地址()[0]).getIntValue();
                    int lowGridValue = CacheManager.getInstance().get(Frame.总并网发电量地址()[1]).getIntValue();
                    long resultGridValue = highGridValue * 65536 + lowGridValue;
                    helper.setText(R.id.tv_total_grid_generation, decimalFormat.format(resultGridValue / Math.pow(10, 1)));
                }


                //PV
                if (null != CacheManager.getInstance().get(Frame.PV日发电量地址())
                        && !StringUtils.isEmpty(CacheManager.getInstance().get(Frame.PV日发电量地址()).getParseValue())) {
                    helper.setText(R.id.tv_daily_pv,
                            CacheManager.getInstance().get(Frame.PV日发电量地址()).getParseValue()
                    );
                }

                if (null != CacheManager.getInstance().get(Frame.PV总发电量地址()[0])) {
                    int highPvValue = CacheManager.getInstance().get(Frame.PV总发电量地址()[0]).getIntValue();
                    int lowPvValue = CacheManager.getInstance().get(Frame.PV总发电量地址()[1]).getIntValue();
                    long resultPvValue = highPvValue * 65536 + lowPvValue;
                    helper.setText(R.id.tv_total_pv, decimalFormat.format(resultPvValue / Math.pow(10, 1)));
                }

                break;
            case MonitorEntity.CENTER_TITLE:
                MonitorEntity<String> centerTitleItem = item;
                helper.setText(R.id.tv_center_title, centerTitleItem.getData());
                break;
            case MonitorEntity.LEFT_TITLE:
                MonitorEntity<String> leftTitleItem = item;
                helper.setText(R.id.tv_left_title, leftTitleItem.getData());
                break;

            case MonitorEntity.MARGIN:

                break;
            case MonitorEntity.SIMPLE_DATA:
                MonitorEntity<PointInfo> pointInfoItem = item;
                helper.setText(R.id.tv_name, pointInfoItem.getData().getDescription());
                int address = Integer.valueOf(pointInfoItem.getData().getAddress());


                if (null != CacheManager.getInstance().get(address)) {
                    DeviceData deviceData = CacheManager.getInstance().get(Integer.valueOf(pointInfoItem.getData().getAddress()));
                    switch (address) {
                        case 4850:
                            helper.setText(R.id.tv_value, Frame.getDeviceTypeName(deviceData.getIntValue()));
                            break;

                        case 4852:
                            helper.setText(R.id.tv_value, Frame.getProtocolTypeName(deviceData.getIntValue()));
                            break;
                        default:
                            helper.setText(R.id.tv_value, (deviceData.getParseValue() + " " + pointInfoItem.getData().getUnit()).trim());
                            break;
                    }

                    if (address == Frame.电池状态地址()) {
                        helper.setText(R.id.tv_value, Frame.getBatStatusName(deviceData.getIntValue()));
                    }
                } else {
                    helper.setText(R.id.tv_value, "--");
                }
                break;

            case MonitorEntity.TABLE_HEAD:
                MonitorEntity<String[]> tableHeadItem = item;
                LinearLayout titleContainer = helper.getView(R.id.ll_title);
                String[] titles = tableHeadItem.getData();
                for (int i = 0; i < titles.length; i++) {
                    ((TextView) titleContainer.getChildAt(i)).setText(titles[i]);
                }
                break;

            case MonitorEntity.TABLE_CONTENT:
                MonitorEntity<String[]> tableContentItem = item;
                LinearLayout contentContainer = helper.getView(R.id.ll_content);
                for(int i = 0;i<contentContainer.getChildCount();i++){
                    ((TextView) contentContainer.getChildAt(i)).setText("");
                }

                String[] contents = tableContentItem.getData();
                for (int i = 0; i < contents.length; i++) {
                    if (i < 1)
                        ((TextView) contentContainer.getChildAt(i)).setText(contents[i]);
                    else if (CacheManager.getInstance().get(Integer.valueOf(contents[i])) != null) {
                        ((TextView) contentContainer.getChildAt(i)).setText(CacheManager.getInstance().get(Integer.valueOf(contents[i])).getParseValue());
                    } else {
                        ((TextView) contentContainer.getChildAt(i)).setText("");
                    }
                }
                break;
        }
    }

    public void destroy() {
        if (mEnergyFlowChart != null)
            mEnergyFlowChart.releaseAll();
    }

}


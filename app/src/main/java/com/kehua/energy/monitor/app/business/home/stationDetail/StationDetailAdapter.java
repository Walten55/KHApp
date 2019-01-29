package com.kehua.energy.monitor.app.business.home.stationDetail;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyco.roundview.RoundTextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.hwangjr.rxbus.RxBus;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.business.home.HomeAdapter;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.configuration.GlideApp;
import com.kehua.energy.monitor.app.model.entity.PerData;
import com.kehua.energy.monitor.app.model.entity.StationEntity;
import com.kehua.energy.monitor.app.utils.linechart.ChartUtils;

import java.util.List;

import me.walten.fastgo.common.Fastgo;

import static me.walten.fastgo.common.Fastgo.getContext;

/**
 * Created by linyixian on 2019/1/24.
 */

public class StationDetailAdapter extends BaseMultiItemQuickAdapter<StationEntity, BaseViewHolder> {
    public StationDetailAdapter(@Nullable List<StationEntity> data) {
        super(data);
        addItemType(StationEntity.OVERVIEW, R.layout.item_station_detail_overview);
        addItemType(StationEntity.LEFT_TITLE, R.layout.item_left_title);
        addItemType(StationEntity.OPERA_DATA, R.layout.item_station_detail_operational);
        addItemType(StationEntity.ENVIRONMENT, R.layout.item_station_detail_environment);
        addItemType(StationEntity.DEVICE_ITEM, R.layout.item_home_4);
    }


    @Override
    protected void convert(final BaseViewHolder helper, StationEntity item) {
        switch (item.getItemType()) {
            case StationEntity.OVERVIEW:

                break;
            case StationEntity.LEFT_TITLE:
                String text = (String) item.getData();
                helper.setText(R.id.tv_name, text);

                if (Fastgo.getContext().getString(R.string.运行数据).equals(text)) {
                    helper.setVisible(R.id.tv_title_right, true);
                    helper.setText(R.id.tv_title_right, Fastgo.getContext().getString(R.string.更多));
                } else if (Fastgo.getContext().getString(R.string.环境贡献).equals(text)) {
                    helper.setVisible(R.id.tv_title_right, false);

                } else if (Fastgo.getContext().getString(R.string.设备信息).equals(text)) {
                    helper.setVisible(R.id.tv_title_right, true);
                    helper.setText(R.id.tv_title_right, Fastgo.getContext().getString(R.string.添加设备));
                } else {
                    helper.setVisible(R.id.tv_title_right, false);
                }

                helper.addOnClickListener(R.id.tv_title_right);
                break;
            case StationEntity.OPERA_DATA:
                if (item.getData() != null && item.getData() instanceof List) {
                    LineChart lineChart = (LineChart) helper.getView(R.id.linechart);
                    ChartUtils.setLineChartStyle(lineChart);
                    ChartUtils.setLineChartData(lineChart, (List<PerData>) item.getData(), false);

                    lineChart.setMarker(new MarkerView(helper.itemView.getContext(), android.R.layout.simple_list_item_1) {
                        @Override
                        public void refreshContent(Entry e, Highlight highlight) {
                            super.refreshContent(e, highlight);
                            int value = (int) (e.getY() * 100);
                            helper.setText(R.id.tv_select_valiue, value + "kW.h");
                        }
                    });
                }
                break;
            case StationEntity.ENVIRONMENT:
                break;
            case StationEntity.DEVICE_ITEM:
                GlideApp.with(getContext())
                        .load(HomeAdapter.url)
                        .transforms(new CenterCrop(), new RoundedCorners(ConvertUtils.dp2px(getContext().getResources().getDimension(R.dimen.grid_2))))
                        .into((ImageView) helper.getView(R.id.iv_img));
                break;

        }
    }
}

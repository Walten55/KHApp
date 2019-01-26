package com.kehua.energy.monitor.app.utils.linechart;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.model.entity.PerData;
import com.kehua.energy.monitor.app.utils.CommonAxisValueFormatter;
import com.kehua.energy.monitor.app.utils.OutPowerAxisValueFormatter;


import java.util.ArrayList;
import java.util.List;

import me.walten.fastgo.common.Fastgo;

/**
 * Created by walten on 2017/11/23.
 */

public class ChartUtils {
    /**
     * @author walten
     * @time 2017/11/23  15:15
     * @describe 设置统一风格
     */
    public static void setLineChartStyle(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragDecelerationFrictionCoef(0.9f);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(false);
        chart.setPinchZoom(false);
        chart.setNoDataText(Fastgo.getContext().getString(R.string.no_data));
        chart.setNoDataTextColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_text));

        Legend l = chart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false);
        xAxis.setTextSize(8f);
        xAxis.setTextColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_text));
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_line));
        xAxis.setGridColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_line));
        xAxis.setGridLineWidth(0.5f);
        xAxis.setGranularity(1);
//        xAxis.setLabelCount(6);
//        xAxis.setSpaceMax(1);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_text));
        leftAxis.setGridColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_line));
        leftAxis.setAxisLineColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_line));
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setAxisMinimum(0);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setLabelCount(4, true);
        leftAxis.setEnabled(false);
//        leftAxis.setXOffset(8);
    }

    /**
     * @author walten
     * @time 2017/11/24  19:28
     * @describe 设置统一风格
     */
    public static void setBarChartStyle(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDragDecelerationFrictionCoef(0.9f);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(false);
        chart.setPinchZoom(false);
        chart.setFitBars(true);
        chart.setNoDataText(Fastgo.getContext().getString(R.string.no_data));
        chart.setNoDataTextColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_text));

        Legend l = chart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(8f);
        xAxis.setTextColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_text));
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_line));
        xAxis.setGridColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_line));
        xAxis.setGridLineWidth(0.5f);
        xAxis.setGranularity(1);
        xAxis.setSpaceMax(1);
        xAxis.setSpaceMin(1);
        xAxis.setLabelCount(12);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_text));
        leftAxis.setGridColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_line));
        leftAxis.setAxisLineColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_line));
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setAxisMinimum(0);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setLabelCount(4, true);
        leftAxis.setEnabled(true);
        leftAxis.setXOffset(8);
    }

    /**
     * @author walten
     * @time 2017/11/24  19:28
     * @describe 设置统一风格
     */
    public static void setLineDataSetStyle(LineDataSet set) {
        set.setColor(ContextCompat.getColor(Fastgo.getContext(), R.color.energy_data_dark));
        set.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(Fastgo.getContext(), R.drawable.fade_blue);
            set.setFillDrawable(drawable);
        } else {
            set.setFillColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_line));
        }
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(1f);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setHighlightEnabled(true);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setHighLightColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_text));

    }

    /**
     * @author walten
     * @time 2017/11/24  19:28
     * @describe 设置统一风格
     */
    public static void setBarDataSetStyle(BarDataSet set) {
        set.setColor(ContextCompat.getColor(Fastgo.getContext(), R.color.energy_data_dark));
        set.setHighlightEnabled(true);
        set.setHighLightColor(ContextCompat.getColor(Fastgo.getContext(), R.color.chart_text));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawValues(false);
    }

    /**
     * @author walten
     * @time 2017/11/24  19:29
     * @describe 设置数据
     */
    public static void setBarChartData(BarChart mChart, List<PerData> list) {
        clearData(mChart);

        List<BarEntry> yVals = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            yVals.add(new BarEntry(i, list.get(i).getVal(), list.get(i)));
        }

        BarDataSet barDataSet;
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            barDataSet = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            barDataSet.setValues(yVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            barDataSet = new BarDataSet(yVals, "DataSet");
            ChartUtils.setBarDataSetStyle(barDataSet);
        }

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.3f);

        mChart.getXAxis().setValueFormatter(new CommonAxisValueFormatter(list));
        mChart.setData(barData);
    }

    /**
     * @author walten
     * @time 2017/11/24  19:29
     * @describe 设置数据
     */
    public static void setLineChartData(LineChart mChart, List<PerData> list, boolean showHightLineShow) {
        if (list == null)
            return;

        clearData(mChart);

        List<Entry> yVals = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            yVals.add(new Entry(i, list.get(i).getVal(), list.get(i)));
        }

        mChart.getXAxis().setValueFormatter(new OutPowerAxisValueFormatter(list));

        LineDataSet lineDataSet;
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            lineDataSet = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            lineDataSet.setValues(yVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            lineDataSet = new LineDataSet(yVals, null);
            ChartUtils.setLineDataSetStyle(lineDataSet);

            lineDataSet.setDrawVerticalHighlightIndicator(showHightLineShow);//取消纵向辅助线
            lineDataSet.setDrawHorizontalHighlightIndicator(showHightLineShow);//取消横向辅助线
            if (showHightLineShow) {
                lineDataSet.setCircleRadius(1);
                lineDataSet.setCircleColor(Color.WHITE);
                lineDataSet.setDrawCircles(true);
            }
        }

        LineData lineData = new LineData(lineDataSet);
        lineData.setValueTextColor(Color.GRAY);
        lineData.setValueTextSize(9f);

        // set data
        mChart.setData(lineData);
    }

    public static void setLineChartData(LineChart mChart, List<PerData> list) {
        setLineChartData(mChart, list, false);
    }


    /**
     * @author walten
     * @time 2017/11/24  19:29
     * @describe 清除数据
     */
    public static void clearData(LineChart chart) {
        if (chart.getData() != null)
            chart.getData().clearValues();
        chart.clear();
    }

    /**
     * @author walten
     * @time 2017/11/24  19:29
     * @describe 清除数据
     */
    public static void clearData(BarChart chart) {
        if (chart.getData() != null)
            chart.getData().clearValues();
        chart.clear();
    }

}

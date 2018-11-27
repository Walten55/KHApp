package com.kehua.energy.monitor.app.view.MPChartHelp;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * 自定义数据规范集合
 * Created by linyixian on 2018/11/27.
 */

public class CusLineChartValueFormat implements IValueFormatter {

    int dataIndex = 0;

    String valueTag = "";

    public CusLineChartValueFormat(String valueTag) {
        this.valueTag = valueTag;
    }


    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return valueTag + (++dataIndex);
    }
}

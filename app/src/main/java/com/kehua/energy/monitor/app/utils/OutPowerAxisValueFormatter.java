package com.kehua.energy.monitor.app.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.kehua.energy.monitor.app.model.entity.PerData;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 */
public class OutPowerAxisValueFormatter implements IAxisValueFormatter
{

    private List<PerData> mList;

    SimpleDateFormat sdfAll =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdfSingle =  new SimpleDateFormat("HH:mm");
    public OutPowerAxisValueFormatter(List<PerData> list){
        mList = list;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if(value<0)
            return "";
        String date = mList.get(((int)value)%mList.size()).getTs();
        try {
            return  sdfSingle.format(sdfAll.parse(date));
        } catch (Exception e) {
            return  date;
        }
    }

}
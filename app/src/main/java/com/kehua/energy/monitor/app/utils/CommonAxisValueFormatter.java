package com.kehua.energy.monitor.app.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.model.entity.PerData;

import java.text.SimpleDateFormat;
import java.util.List;

import me.walten.fastgo.common.Fastgo;

/**
 */
public class CommonAxisValueFormatter implements IAxisValueFormatter
{

    private String mUnit;
    private int mSize;
    private int mType;
    private List<PerData> mList;
    public static SimpleDateFormat sdfAll =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat sdfSingle =  new SimpleDateFormat("yyyy");
    public CommonAxisValueFormatter(List<PerData> list){
        mList = list;
        mSize = list.size();
        if(mSize==12){
            mUnit = Fastgo.getContext().getString(R.string.月);
            mType = 2;
        }

        else if(mSize>12&&mSize<32){
            mUnit = Fastgo.getContext().getString(R.string.日);
            mType = 1;
        }

        else{
            mUnit = Fastgo.getContext().getString(R.string.年);
            mType = 3;
        }

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if(value<0)
            return "";
        String date = mList.get(((int)value)%mSize).getTs();
        try {
//            if((value+1)>=mSize){
//                return mType!=3? (int)value+1>mSize?""
//                        :(int)value+1+mUnit
//                        :(int)value+1>mSize?""
//                        :sdfSingle.format(sdfAll.parse(date));
//            }else {
                return mType!=3? (int)value+1>mSize?""
                        :(int)value+1+""
                        :(int)value+1>mSize?""
                        :sdfSingle.format(sdfAll.parse(date));
//            }

        } catch (Exception e) {
            return "";
        }
    }

}
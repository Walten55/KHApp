package com.kehua.energy.monitor.app.business.local.history;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.model.entity.RecordData;

import java.util.List;

/*
 * -----------------------------------------------------------------
 * Copyright by 2018 Walten, All rights reserved.
 * -----------------------------------------------------------------
 * desc:
 * -----------------------------------------------------------------
 * 2018/10/22 : Create HistoryAdapter.java (Walten);
 * -----------------------------------------------------------------
 */
public class HistoryAdapter extends BaseQuickAdapter<RecordData,BaseViewHolder> {
    public HistoryAdapter( @Nullable List<RecordData> data) {
        super(R.layout.item_local_record, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RecordData item) {
        if(item.getName().contains("HH_HH")){
            helper.setText(R.id.tv_name,item.getName().split("HH_HH")[0].trim());
        }else {
            helper.setText(R.id.tv_name,item.getName().trim());
        }

        helper.setText(R.id.tv_time,item.getTime().trim());
        helper.setText(R.id.tv_device_address,mContext.getString(R.string.机器编号_冒号)+ LocalUserManager.getSn());
        helper.setText(R.id.tv_value,item.getParseValue().trim());
        if((item.getTemplate()==0||item.getTemplate()==2)&&item.isSwitch()&&item.getCode()!=6200){
            helper.setTextColor(R.id.tv_value,item.getValue()==0?
                    ContextCompat.getColor(mContext,R.color.green):
                    ContextCompat.getColor(mContext,R.color.red));
        }else if((item.getTemplate()==3)&&item.isSwitch()&&item.getCode()!=6200){
            helper.setTextColor(R.id.tv_value,item.getValue()==0?
                    ContextCompat.getColor(mContext,R.color.red):
                    ContextCompat.getColor(mContext,R.color.green));
        }else {
            helper.setTextColor(R.id.tv_value, ContextCompat.getColor(mContext,R.color.text_black));
        }

        if(item.getCode()==6020){
            int year = item.getValue()/32140800 - 1;
            int month = (item.getValue()-32140800*year)/2678400;
            if(month>12){
                year+=1;
                month = (item.getValue()-32140800*year)/2678400;
            }
            int day = (item.getValue()-32140800*year-2678400*month)/86400;
            int hour = (item.getValue()-32140800*year-2678400*month-86400*day)/3600;
            int min = (item.getValue()-32140800*year-2678400*month-86400*day-3600*hour)/60;
            int second = item.getValue()%60;

            String yearS= year+2000+"";
            String monthS = month<10?"0"+month:""+month;
            String dayS = day<10?"0"+day:""+day;
            String hourS = hour<10?"0"+hour:""+hour;
            String minS = min<10?"0"+min:""+min;
            String secondS = second<10?"0"+second:""+second;

            helper.setText(R.id.tv_value,yearS+"-"+monthS+"-"+dayS+" "+hourS+":"+minS+":"+secondS);
        }

    }
}

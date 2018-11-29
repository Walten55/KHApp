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
        if(item.isSwitch()&&item.getCode()!=6200){
            helper.setTextColor(R.id.tv_value,item.getValue()==0?
                    ContextCompat.getColor(mContext,R.color.green):
                    ContextCompat.getColor(mContext,R.color.red));
        }else {
            helper.setTextColor(R.id.tv_value, ContextCompat.getColor(mContext,R.color.text_black));
        }

    }
}

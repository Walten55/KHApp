package com.kehua.energy.monitor.app.business.local.alarm;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyco.roundview.RoundTextView;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.model.entity.DeviceData;

import java.util.List;

import me.walten.fastgo.common.Fastgo;

public class LocalAlarmAdapter extends BaseQuickAdapter<DeviceData, BaseViewHolder> {


    public LocalAlarmAdapter(@Nullable List<DeviceData> data) {
        super(R.layout.item_local_alarm, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceData item) {
        helper.setText(R.id.tv_local_alarm_name, item.getDescription());

        int colorId = Fastgo.getContext().getString(R.string.告警_标识).equals(item.getSgroup()) ? R.color.yellow: R.color.red ;

        RoundTextView rtvAlarmValue = helper.getView(R.id.tv_local_alarm_value);
        rtvAlarmValue.getDelegate().setBackgroundColor(Fastgo.getContext().getResources().getColor(colorId));
    }
}

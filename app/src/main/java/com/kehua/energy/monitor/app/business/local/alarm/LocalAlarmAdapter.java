package com.kehua.energy.monitor.app.business.local.alarm;

import android.content.Context;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyco.roundview.RoundTextView;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.model.entity.DeviceData;

import java.lang.ref.WeakReference;
import java.util.List;

import me.walten.fastgo.common.Fastgo;

public class LocalAlarmAdapter extends BaseQuickAdapter<DeviceData, BaseViewHolder> {

    WeakReference<Context> localContext = new WeakReference<Context>(ActivityUtils.getTopActivity() == null ? Fastgo.getContext() : ActivityUtils.getTopActivity());
    
    public LocalAlarmAdapter(@Nullable List<DeviceData> data) {
        super(R.layout.item_local_alarm, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceData item) {
        helper.setText(R.id.tv_local_alarm_name, item.getDescriptionCN());

        int colorId = localContext.get().getString(R.string.告警).equals(item.getSgroup()) ? R.color.yellow: R.color.red ;

        RoundTextView rtvAlarmValue = helper.getView(R.id.tv_local_alarm_value);
        rtvAlarmValue.getDelegate().setBackgroundColor(Fastgo.getContext().getResources().getColor(colorId));
    }
}

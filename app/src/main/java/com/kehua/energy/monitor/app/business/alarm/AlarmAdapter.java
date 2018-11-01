package com.kehua.energy.monitor.app.business.alarm;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.configuration.GlideApp;
import com.kehua.energy.monitor.app.model.entity.Alarm;

import java.util.List;

import me.walten.fastgo.common.Fastgo;

import static me.walten.fastgo.common.Fastgo.getContext;

public class AlarmAdapter extends BaseQuickAdapter<Alarm, BaseViewHolder> {
    public AlarmAdapter(@Nullable List<Alarm> data) {
        super(R.layout.item_alarm, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Alarm item) {
        GlideApp.with(Fastgo.getContext())
                .load(item.getImgUrl())
                .transforms(new CenterCrop(),new RoundedCorners(ConvertUtils.dp2px(getContext().getResources().getDimension(R.dimen.grid_2))))
                .into((ImageView) helper.getView(R.id.iv_alarm_icon));

        helper.setText(R.id.tv_alarm_name, item.getName());
        helper.setText(R.id.tv_alarm_belong_site, Fastgo.getContext().getResources().getString(R.string.所属电站_冒号) + item.getName());
        helper.setText(R.id.tv_alarm_time, Fastgo.getContext().getResources().getString(R.string.告警时间_冒号) + item.getTime());
    }
}

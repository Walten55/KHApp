package com.kehua.energy.monitor.app.business.local.setting.pattern.patternModelChild;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.model.entity.PointInfo;

import java.util.List;

/**
 * Created by linyixian on 2018/10/9.
 */

public class PatternChildAdapter extends BaseQuickAdapter<PointInfo, BaseViewHolder> {
    public PatternChildAdapter(@Nullable List<PointInfo> data) {
        super(R.layout.item_local_setting_text, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PointInfo item) {

        helper.setText(R.id.tv_name, item.getDescription());

        DeviceData deviceData = CacheManager.getInstance().get(Integer.valueOf(item.getAddress().trim()));

        if (deviceData != null) {
            helper.setText(R.id.tv_value, deviceData.getParseValue() + deviceData.getUnit());
            helper.setGone(R.id.tv_reading, false);
            helper.setGone(R.id.tv_value, true);
        } else {
            helper.setGone(R.id.tv_reading, true);
            helper.setGone(R.id.tv_value, false);
        }

//        helper.setGone(R.id.view_bottom_line, false);
//        ((TextView) helper.getView(R.id.tv_name)).setTextSize(helper.itemView.getResources().getDimension(R.dimen.grid_4));
//        ((TextView) helper.getView(R.id.tv_value)).setTextSize(helper.itemView.getResources().getDimension(R.dimen.grid_4));

    }
}

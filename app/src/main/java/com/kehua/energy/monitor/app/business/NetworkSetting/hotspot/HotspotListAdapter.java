package com.kehua.energy.monitor.app.business.NetworkSetting.hotspot;

import android.content.Context;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.model.entity.HotspotInfo;
import com.kehua.energy.monitor.app.utils.WiFiUtils;

import java.util.List;

import me.walten.fastgo.common.Fastgo;

public class HotspotListAdapter extends BaseQuickAdapter<HotspotInfo,BaseViewHolder>{

    Context localContext = ActivityUtils.getTopActivity() == null
            ? Fastgo.getContext() : ActivityUtils.getTopActivity();
    
    public HotspotListAdapter(@Nullable List<HotspotInfo> data) {
        super(R.layout.item_hotspot,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HotspotInfo item) {
        helper.setText(R.id.tv_ssid,item.getSsid());

        if(item.getLevel()>=-80){
            helper.setText(R.id.tv_level, localContext.getString(R.string.信号强));
        }else if(-80>item.getLevel() && item.getLevel()>=-110){
            helper.setText(R.id.tv_level,localContext.getString(R.string.信号一般));
        }else {
            helper.setText(R.id.tv_level,localContext.getString(R.string.信号弱));
        }

        helper.addOnClickListener(R.id.tv_connect);
    }
}

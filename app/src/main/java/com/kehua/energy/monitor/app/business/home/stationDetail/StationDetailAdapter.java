package com.kehua.energy.monitor.app.business.home.stationDetail;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyco.roundview.RoundTextView;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.business.home.HomeAdapter;
import com.kehua.energy.monitor.app.configuration.GlideApp;
import com.kehua.energy.monitor.app.model.entity.HomeEntity;
import com.kehua.energy.monitor.app.model.entity.StationEntity;

import java.util.List;

import me.walten.fastgo.common.Fastgo;

import static me.walten.fastgo.common.Fastgo.getContext;

/**
 * Created by linyixian on 2019/1/24.
 */

public class StationDetailAdapter extends BaseMultiItemQuickAdapter<StationEntity, BaseViewHolder> {
    public StationDetailAdapter(@Nullable List<StationEntity> data) {
        super(data);
        addItemType(StationEntity.OVERVIEW, R.layout.item_station_detail_overview);
        addItemType(StationEntity.LEFT_TITLE, R.layout.item_home_2);
        addItemType(StationEntity.OPERA_DATA, R.layout.item_station_detail_operational);
        addItemType(StationEntity.ENVIRONMENT, R.layout.item_station_detail_environment);
        addItemType(StationEntity.DEVICE_ITEM, R.layout.item_home_4);
    }


    @Override
    protected void convert(BaseViewHolder helper, StationEntity item) {
        switch (item.getItemType()) {
            case StationEntity.OVERVIEW:

                break;
            case StationEntity.LEFT_TITLE:
                String text = (String) item.getData();
                helper.setText(R.id.tv_name, text);

                RoundTextView rightRtv = helper.getView(R.id.tv_all);
                rightRtv.getDelegate().setCornerRadius(5);
                rightRtv.setTextColor(ContextCompat.getColor(Fastgo.getContext(), R.color.white));
                rightRtv.getDelegate().setBackgroundColor(ContextCompat.getColor(Fastgo.getContext(), R.color.green));

                helper.setGone(R.id.view_right_margin, true);

                if (Fastgo.getContext().getString(R.string.运行数据).equals(text)) {
                    helper.setVisible(R.id.tv_all, true);

                } else if (Fastgo.getContext().getString(R.string.环境贡献).equals(text)) {
                    helper.setVisible(R.id.tv_all, true);

                } else if (Fastgo.getContext().getString(R.string.设备信息).equals(text)) {
                    helper.setVisible(R.id.tv_all, false);
                } else {
                    helper.setVisible(R.id.tv_all, false);
                }
                break;
            case StationEntity.OPERA_DATA:
                break;
            case StationEntity.ENVIRONMENT:
                break;
            case StationEntity.DEVICE_ITEM:
                GlideApp.with(getContext())
                        .load(HomeAdapter.url)
                        .transforms(new CenterCrop(),new RoundedCorners(ConvertUtils.dp2px(getContext().getResources().getDimension(R.dimen.grid_2))))
                        .into((ImageView) helper.getView(R.id.iv_img));
                break;

        }
    }
}

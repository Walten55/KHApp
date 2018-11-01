package com.kehua.energy.monitor.app.business.home;

import android.widget.ImageView;

import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.configuration.GlideApp;
import com.kehua.energy.monitor.app.configuration.GlideImageLoader;
import com.kehua.energy.monitor.app.model.entity.HomeEntity;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import static me.walten.fastgo.common.Fastgo.getContext;

public class HomeAdapter extends BaseMultiItemQuickAdapter<HomeEntity, BaseViewHolder> {
    final String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536209922460&di=e481f7025eb9d102c79ad1ad3147944e&imgtype=0&src=http%3A%2F%2Fww2.sinaimg.cn%2Fcrop.0.83.800.449.1000.562%2F006ahoQKgw1f72mtqyci6j30m80etjwo.jpg";

    public HomeAdapter(List data) {
        super(data);
        addItemType(HomeEntity.OVERVIEW, R.layout.item_home_1);
        addItemType(HomeEntity.LEFT_TITLE, R.layout.item_home_2);
        addItemType(HomeEntity.POWER_STATION_ITEM, R.layout.item_home_3);
        addItemType(HomeEntity.DEVICE_ITEM, R.layout.item_home_4);
        addItemType(HomeEntity.CREATE_BUTTON, R.layout.item_home_5);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeEntity item) {
        switch (helper.getItemViewType()) {
            case HomeEntity.OVERVIEW:
                List<String> urls = new ArrayList<>();
                urls.add(url);
                Banner banner = helper.getView(R.id.banner);
                //设置图片加载器
                banner.setImageLoader(new GlideImageLoader());
                //设置图片集合
                banner.setImages(urls);
                //banner设置方法全部调用完毕时最后调用
                banner.start();
                break;
            case HomeEntity.LEFT_TITLE:
                HomeEntity<String> newItem = item;
                helper.setText(R.id.tv_name, newItem.getData());
                break;
            case HomeEntity.POWER_STATION_ITEM:


                GlideApp.with(getContext())
                        .load(url)
                        .transforms(new CenterCrop(),new RoundedCorners(ConvertUtils.dp2px(getContext().getResources().getDimension(R.dimen.grid_2))))
                        .into((ImageView) helper.getView(R.id.iv_img));
                break;
            case HomeEntity.DEVICE_ITEM:

                GlideApp.with(getContext())
                        .load(url)
                        .transforms(new CenterCrop(),new RoundedCorners(ConvertUtils.dp2px(getContext().getResources().getDimension(R.dimen.grid_2))))
                        .into((ImageView) helper.getView(R.id.iv_img));

                break;
            case HomeEntity.CREATE_BUTTON:

                break;
        }
    }

}


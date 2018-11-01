package com.kehua.energy.monitor.app.business.favorite;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.configuration.GlideApp;

import java.util.List;

import me.walten.fastgo.common.Fastgo;

import static me.walten.fastgo.common.Fastgo.getContext;

public class FavoriteAdapter extends BaseQuickAdapter<String,BaseViewHolder> {
    final String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536209922460&di=e481f7025eb9d102c79ad1ad3147944e&imgtype=0&src=http%3A%2F%2Fww2.sinaimg.cn%2Fcrop.0.83.800.449.1000.562%2F006ahoQKgw1f72mtqyci6j30m80etjwo.jpg";

    public FavoriteAdapter(@Nullable List<String> data) {
        super(R.layout.item_home_4,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        GlideApp.with(Fastgo.getContext())
                .load(url)
                .transforms(new CenterCrop(),new RoundedCorners(ConvertUtils.dp2px(getContext().getResources().getDimension(R.dimen.grid_2))))
                .centerCrop()
                .into((ImageView) helper.getView(R.id.iv_img));

    }
}

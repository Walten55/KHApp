package com.kehua.energy.monitor.app.view.TopRightMenu;


import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.model.entity.MenuItem;

import java.util.List;

public class TRMenuAdapter extends BaseQuickAdapter<MenuItem, BaseViewHolder> {


    public TRMenuAdapter(@Nullable List<MenuItem> data) {
        super(R.layout.item_alarm_status, data);

    }

    @Override
    protected void convert(BaseViewHolder helper, MenuItem item) {
        if (helper.getAdapterPosition() == 0) {
            int marginTop = ConvertUtils.dp2px(mContext.getResources().getDimension(R.dimen.grid_1));
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) helper.itemView.getLayoutParams();
            params.setMargins(params.leftMargin, marginTop, params.rightMargin, params.bottomMargin);
            helper.itemView.setLayoutParams(params);
        } else if (helper.getAdapterPosition() == getItemCount() - 1) {
            int marginBottom = ConvertUtils.dp2px(mContext.getResources().getDimension(R.dimen.grid_1));
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) helper.itemView.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, marginBottom);
            helper.itemView.setLayoutParams(params);

            helper.setBackgroundColor(R.id.tv_status_name, ContextCompat.getColor(helper.itemView.getContext(), R.color.transparent));
        }
        helper.setText(R.id.tv_status_name, item.getText());
    }

}

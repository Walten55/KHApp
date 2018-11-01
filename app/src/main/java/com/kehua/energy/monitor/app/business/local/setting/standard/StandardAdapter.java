package com.kehua.energy.monitor.app.business.local.setting.standard;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.business.local.setting.advanced.AdvancedPresenter;
import com.kehua.energy.monitor.app.model.entity.Standard;

import java.util.List;

public class StandardAdapter extends BaseQuickAdapter<Standard, BaseViewHolder> {

    private AdvancedPresenter presenter;

    public StandardAdapter(List data) {
        super(R.layout.item_standard, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Standard item) {
        helper.setImageResource(R.id.iv_img, item.getImageResId());
        helper.setText(R.id.tv_name, item.getName());
    }
}


package com.kehua.energy.monitor.app.business.local.setting.branch;

import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.View;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kehua.energy.monitor.app.R;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import me.walten.fastgo.common.Fastgo;

public class BranchSettingAdapter extends BaseQuickAdapter<ArrayMap<String,Boolean>,BaseViewHolder> {

    private int address;
    private List<String> itemName = new ArrayList<>();

    public BranchSettingAdapter(@Nullable List<ArrayMap<String,Boolean>> data) {
        super(R.layout.item_local_setting_switch_with_hint,data);
    }

    public void setAddress(int address){
        this.address = address;
        itemName.clear();
        if(address == 6305){
            //支路告警屏蔽
            for(int i = 1;i<=16;i++){
                itemName.add(Fastgo.getContext().getString(R.string.支路)+" "+i);
            }
        }else if(address == 6309){
            //pv支路使能字
            for(int i = 1;i<=16;i++){
                itemName.add(Fastgo.getContext().getString(R.string.PV支路)+" "+i+" "+Fastgo.getContext().getString(R.string.使能));
            }
        }
    }

    @Override
    protected void convert(final BaseViewHolder helper, ArrayMap<String,Boolean> item) {
        helper.setText(R.id.tv_name,itemName.get(helper.getAdapterPosition()));
        if(address == 6305) {
            //支路告警屏蔽 off 使能 on 屏蔽
            helper.setText(R.id.tv_hint, Fastgo.getContext().getString(R.string.支路告警屏蔽解释));
        }else if(address == 6309) {
            //pv支路使能字 off 禁止 on 使能
            helper.setText(R.id.tv_hint,Fastgo.getContext().getString(R.string.PV支路使能字解释));
        }


        final SwitchButton switchButton = helper.getView(R.id.sb_value);
        switchButton.setVisibility(View.VISIBLE);
        switchButton.setCheckedImmediatelyNoEvent(item.get("boolean"));
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mData.get(helper.getAdapterPosition()).put("boolean",b);
                notifyDataSetChanged();
            }
        });
    }
}


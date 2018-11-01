package com.kehua.energy.monitor.app.model.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class SettingEntity implements MultiItemEntity {

    public static final int SWITCH = 1;
    public static final int TEXT = 2;

    private int itemType = 0;

    private PointInfo data;

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType){
        this.itemType = itemType;
    }

    public PointInfo getData() {
        return data;
    }

    public void setData(PointInfo data) {
        this.data = data;
    }

    public SettingEntity(PointInfo data) {
        this.itemType = "boolean_int".equals(data.getDataType())?SWITCH:TEXT;
        this.data = data;
    }
}

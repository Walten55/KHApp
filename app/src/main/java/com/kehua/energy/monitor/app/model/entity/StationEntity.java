package com.kehua.energy.monitor.app.model.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;


public class StationEntity<T> implements MultiItemEntity {

    public static final int OVERVIEW = 1;
    public static final int LEFT_TITLE = 2;
    public static final int OPERA_DATA = 3;
    public static final int ENVIRONMENT = 4;
    public static final int DEVICE_ITEM = 5;

    private int itemType = LEFT_TITLE;

    private T data;

    public StationEntity(int itemType, T data) {
        this.itemType = itemType;
        this.data = data;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

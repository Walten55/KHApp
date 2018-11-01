package com.kehua.energy.monitor.app.model.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class HomeEntity<T> implements MultiItemEntity {

    public static final int OVERVIEW = 1;
    public static final int LEFT_TITLE = 2;
    public static final int POWER_STATION_ITEM = 3;
    public static final int DEVICE_ITEM = 4;
    public static final int CREATE_BUTTON = 5;

    private int itemType = 0;

    private T data;

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType){
        this.itemType = itemType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public HomeEntity(int itemType, T data) {
        this.itemType = itemType;
        this.data = data;
    }
}

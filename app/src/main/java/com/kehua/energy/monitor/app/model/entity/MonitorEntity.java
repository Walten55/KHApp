package com.kehua.energy.monitor.app.model.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class MonitorEntity<T> implements MultiItemEntity {

    public static final int OVERVIEW = 1;
    public static final int LEFT_TITLE = 2;
    public static final int MARGIN = 3;
    public static final int CENTER_TITLE = 4;
    public static final int SIMPLE_DATA = 5;
    public static final int TABLE_HEAD = 6;
    public static final int TABLE_CONTENT = 7;
    public static final int SIMPLE_DATA_WITH_ADDRESS = 8;

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

    public MonitorEntity(int itemType, T data) {
        this.itemType = itemType;
        this.data = data;
    }
}

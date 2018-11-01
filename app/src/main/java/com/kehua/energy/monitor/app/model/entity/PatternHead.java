package com.kehua.energy.monitor.app.model.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.kehua.energy.monitor.app.business.local.setting.pattern.PatternAdapter;

import java.util.ArrayList;
import java.util.List;

public class PatternHead extends AbstractExpandableItem implements MultiItemEntity {

    public static final int HEAD_TEXT = PatternAdapter.TYPE_HEAD_TEXT;

    public static final int HEAD_SWITCH = PatternAdapter.TYPE_HEAD_SWITCH;

    int itemType = HEAD_TEXT;

    PointInfo pointInfo;

    boolean forLineChart = false;

    List<PointInfo> subItemData = new ArrayList<>();

    public PatternHead(PointInfo pointInfo) {
        this.itemType = "boolean_int".equals(pointInfo.getDataType()) ? HEAD_SWITCH : HEAD_TEXT;
        this.pointInfo = pointInfo;
    }

    public PatternHead(PointInfo pointInfo, boolean forLineChart) {
        this.itemType = "boolean_int".equals(pointInfo.getDataType()) ? HEAD_SWITCH : HEAD_TEXT;
        this.pointInfo = pointInfo;
        this.forLineChart = forLineChart;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public PointInfo getPointInfo() {
        return pointInfo;
    }

    public void addSubItemData(PointInfo pointInfo) {
        if (subItemData != null) {
            subItemData.add(pointInfo);
        }
    }

    public boolean isForLineChart() {
        return forLineChart;
    }

    public List<PointInfo> getSubItemData() {
        return subItemData;
    }

    public void dealSubItemData() {
        if (subItemData == null) {
            return;
        }

        //数据填充
        if (forLineChart) {

        } else {
            for (PointInfo pointInfo : subItemData) {
                addSubItem(new PatternEntity(pointInfo));
            }
        }
    }
}

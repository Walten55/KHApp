package com.kehua.energy.monitor.app.model.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.kehua.energy.monitor.app.business.local.setting.pattern.PatternAdapter;

import java.util.List;

public class PatternEntity implements MultiItemEntity {

    public static final int TEXT = PatternAdapter.TYPE_CONTENT_TEXT;

    public static final int LINE_CHART = PatternAdapter.TYPE_CONTENT_LINECHART;

    public static final int BLANK = PatternAdapter.TYPE_CONTENT_BLANK;

    int itemType = TEXT;

    String[] lables;
    int[] colors;
    boolean singleLine = true;
    List<PointInfo>[] data;
    String[] valueTags;

    PointInfo pointInfo;

    public PatternEntity(PointInfo pointInfo) {
        this.itemType = TEXT;
        this.pointInfo = pointInfo;
    }

    public PatternEntity(List<PointInfo> singleData) {
        singleLine = true;
        data = new List[]{singleData};
        this.itemType = LINE_CHART;
    }

    public PatternEntity(String[] lables, int[] colors, List<PointInfo>... data) {
        this.data = data;
        this.lables = lables;
        this.colors = colors;
        this.itemType = LINE_CHART;
        singleLine = false;
    }

    public PatternEntity() {
        this.itemType = BLANK;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public String[] getValueTags() {
        return valueTags;
    }

    public void setValueTags(String... valueTags) {
        this.valueTags = valueTags;
    }

    public String[] getLables() {
        return lables;
    }

    public void setLables(String[] lables) {
        this.lables = lables;
    }

    public int[] getColors() {
        return colors;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public boolean isSingleLine() {
        return singleLine;
    }

    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
    }

    public List<PointInfo>[] getData() {
        return data;
    }

    public void setData(List<PointInfo>[] data) {
        this.data = data;
    }

    public PointInfo getPointInfo() {
        return pointInfo;
    }

    public void setPointInfo(PointInfo pointInfo) {
        this.pointInfo = pointInfo;
    }
}

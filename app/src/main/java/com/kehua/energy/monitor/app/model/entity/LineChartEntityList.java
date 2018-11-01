package com.kehua.energy.monitor.app.model.entity;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class LineChartEntityList {
    String des = "des";
    List<LineChartEntity> data = new ArrayList<>();
    int color = Color.GREEN;

    public LineChartEntityList(List<LineChartEntity> data) {
        this.data = data;
    }

    public LineChartEntityList(String des, int color, List<LineChartEntity> data) {
        this.des = des;
        this.data = data;
        this.color = color;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public List<LineChartEntity> getData() {
        return data;
    }

    public void setData(List<LineChartEntity> data) {
        this.data = data;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

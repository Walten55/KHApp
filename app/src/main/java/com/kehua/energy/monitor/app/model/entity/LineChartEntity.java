package com.kehua.energy.monitor.app.model.entity;

import com.github.mikephil.charting.data.Entry;
import com.kehua.energy.monitor.app.utils.Utils;

public class LineChartEntity {

    DeviceData deviceDataX;

    DeviceData deviceDataY;

    Entry entry;

    public LineChartEntity(DeviceData deviceDataX, DeviceData deviceDataY) {
        this.deviceDataX = deviceDataX;
        this.deviceDataY = deviceDataY;

        entry = new Entry();
        if (deviceDataX != null) {
            entry.setX(Utils.isNum(deviceDataX.getParseValue())
                    ? Float.parseFloat(deviceDataX.getParseValue()) : 0);
        }
        if (deviceDataY != null) {
            entry.setY(Utils.isNum(deviceDataY.getParseValue())
                    ? Float.parseFloat(deviceDataY.getParseValue()) : 0);
        }
    }

    public Entry getEntry() {
        return entry;
    }

    public String getXUnit() {
        if (deviceDataX != null) {
            return deviceDataX.getUnit();
        } else {
            return "";
        }
    }

    public String getYUnit() {
        if (deviceDataY != null) {
            return deviceDataY.getUnit();
        } else {
            return "";
        }
    }


}

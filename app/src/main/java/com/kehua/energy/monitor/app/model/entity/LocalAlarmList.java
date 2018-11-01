package com.kehua.energy.monitor.app.model.entity;

import java.util.List;

public class LocalAlarmList {

    long commonAlarmCount;

    long seriousAlarmCount;

    List<DeviceData> deviceDataList;

    public LocalAlarmList() {
    }

    public LocalAlarmList(long commonAlarmCount, long seriousAlarmCount, List<DeviceData> deviceDataList) {
        this.commonAlarmCount = commonAlarmCount;
        this.seriousAlarmCount = seriousAlarmCount;
        this.deviceDataList = deviceDataList;
    }

    public long getCommonAlarmCount() {
        return commonAlarmCount;
    }

    public void setCommonAlarmCount(long commonAlarmCount) {
        this.commonAlarmCount = commonAlarmCount;
    }

    public long getSeriousAlarmCount() {
        return seriousAlarmCount;
    }

    public void setSeriousAlarmCount(long seriousAlarmCount) {
        this.seriousAlarmCount = seriousAlarmCount;
    }

    public List<DeviceData> getDeviceDataList() {
        return deviceDataList;
    }

    public void setDeviceDataList(List<DeviceData> deviceDataList) {
        this.deviceDataList = deviceDataList;
    }
}

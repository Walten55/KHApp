package com.kehua.energy.monitor.app.model.entity;

/*
 * -----------------------------------------------------------------
 * Copyright by 2018 Walten, All rights reserved.
 * -----------------------------------------------------------------
 * desc:
 * -----------------------------------------------------------------
 * 2018/10/22 : Create RecordData.java (Walten);
 * -----------------------------------------------------------------
 */
public class RecordData {
    private int deviceAddress;

    private String name;

    private int code;

    private int value;

    private String parseValue;

    private String time;

    private boolean isSwitch;

    public RecordData(int deviceAddress,String name, int code, int value, String parseValue, String time,boolean isSwitch) {
        this.deviceAddress = deviceAddress;
        this.name = name;
        this.code = code;
        this.value = value;
        this.parseValue = parseValue;
        this.time = time;
        this.isSwitch = isSwitch;
    }

    public boolean isSwitch() {
        return isSwitch;
    }

    public void setSwitch(boolean isSwitch) {
        isSwitch = isSwitch;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getParseValue() {
        return parseValue;
    }

    public void setParseValue(String parseValue) {
        this.parseValue = parseValue;
    }

    public int getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(int deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

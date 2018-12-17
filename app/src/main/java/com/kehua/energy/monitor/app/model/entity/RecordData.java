package com.kehua.energy.monitor.app.model.entity;

import com.blankj.utilcode.util.StringUtils;
import com.kehua.energy.monitor.app.model.local.LocalModel;
import com.kehua.energy.monitor.app.utils.LanguageUtils;

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
    private int template;

    private int deviceAddress;

    private String name;

    private int code;

    private int value;

    private String parseValue;

    private String time;

    private boolean isSwitch;

    public RecordData(int template,int deviceAddress,String name, int code, int value, String parseValue, String time,boolean isSwitch) {
        this.template = template;
        this.deviceAddress = deviceAddress;
        this.name = name;
        this.code = code;
        this.value = value;
        this.parseValue = parseValue;
        this.time = time;
        this.isSwitch = isSwitch;
    }

    public int getTemplate() {
        return template;
    }

    public void setTemplate(int template) {
        this.template = template;
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
        if (!StringUtils.isTrimEmpty(parseValue) && parseValue.contains("##")) {
            String[] values = parseValue.split("##");
            String language = LocalModel.getLanguageSelect();

            String result = "";
            switch (language) {
                case LanguageUtils.Chinese:
                    result = values[0];
                    break;
                case LanguageUtils.English:
                    result = values[1];
                    break;
                default:
                    result = values[0];
                    break;
            }
            return result;
        } else {
            return parseValue;
        }
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

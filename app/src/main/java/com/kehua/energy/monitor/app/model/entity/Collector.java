package com.kehua.energy.monitor.app.model.entity;

/*
 * -----------------------------------------------------------------
 * Copyright by 2018 Walten, All rights reserved.
 * -----------------------------------------------------------------
 * desc:
 * -----------------------------------------------------------------
 * 2018/11/1 : Create Collector.java (Walten);
 * -----------------------------------------------------------------
 */
public class Collector {
//    "sn":	"B47011880016",
//            "key":	"SRHBXQ54MVQRXKH5",
//            "hw":	"AEG4-0002-00",
//            "brd":	"E-Linter",
//            "mod":	"EGEW-0002",
//            "sw":	"4760118905R",
//            "name":	"Magpile"

    private String sn;
    private String key;
    private String hw;
    private String brd;
    private String mod;
    private String sw;
    private String name;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getHw() {
        return hw;
    }

    public void setHw(String hw) {
        this.hw = hw;
    }

    public String getBrd() {
        return brd;
    }

    public void setBrd(String brd) {
        this.brd = brd;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public String getSw() {
        return sw;
    }

    public void setSw(String sw) {
        this.sw = sw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

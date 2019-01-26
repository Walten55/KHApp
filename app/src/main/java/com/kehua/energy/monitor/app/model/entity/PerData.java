package com.kehua.energy.monitor.app.model.entity;


/**
 * @author walten
 * @time 2017/11/15  10:07
 * @describe 集合数据 天每时 月每日 年每月 站每年
 */
public class PerData {

    private float val;
    private String ts = "";
    private String par = "";

    public PerData(float val) {
        this.val = val;
    }

    public String getPar() {
        return par;
    }

    public void setPar(String par) {
        this.par = par;
    }

    public float getVal() {
        return val;
    }

    public void setVal(float val) {
        this.val = val;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}

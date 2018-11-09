package com.kehua.energy.monitor.app.model.entity;

/*
 * -----------------------------------------------------------------
 * Copyright by 2018 Walten, All rights reserved.
 * -----------------------------------------------------------------
 * desc:
 * -----------------------------------------------------------------
 * 2018/11/6 : Create Upgrade.java (Walten);
 * -----------------------------------------------------------------
 */
public class Upgrade {
    private int num;

    private int upprogress;

    private int dnprogress;

    private int status;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getUpprogress() {
        return upprogress;
    }

    public void setUpprogress(int upprogress) {
        this.upprogress = upprogress;
    }

    public int getDnprogress() {
        return dnprogress;
    }

    public void setDnprogress(int dnprogress) {
        this.dnprogress = dnprogress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

package com.kehua.energy.monitor.app.model.entity;

import java.util.List;

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

    private List<InvInfo> inv;

    private int dnprogress;

    private int upprogress;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<InvInfo> getInv() {
        return inv;
    }

    public void setInv(List<InvInfo> inv) {
        this.inv = inv;
    }

    public int getDnprogress() {
        return dnprogress;
    }

    public void setDnprogress(int dnprogress) {
        this.dnprogress = dnprogress;
    }

    public int getUpprogress() {
        return upprogress;
    }

    public void setUpprogress(int upprogress) {
        this.upprogress = upprogress;
    }

    public class InvInfo{
        private int addr;

        private int status;

        public int getAddr() {
            return addr;
        }

        public void setAddr(int addr) {
            this.addr = addr;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}

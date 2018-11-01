package com.kehua.energy.monitor.app.model.entity;

import java.util.List;

public class InvInfoList {

    private int num;

    private List<InvInfo> inv;

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

    public class InvInfo{
        private int addr;

        public int getAddr() {
            return addr;
        }

        public void setAddr(int addr) {
            this.addr = addr;
        }
    }

}

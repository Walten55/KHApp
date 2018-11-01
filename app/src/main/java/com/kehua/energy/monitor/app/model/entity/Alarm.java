package com.kehua.energy.monitor.app.model.entity;

import com.blankj.utilcode.util.StringUtils;

public class Alarm {

    String name;

    String belongSite;

    String time;

    String imgUrl;

    public Alarm() {
    }

    public Alarm(String name, String belongSite, String time, String imgUrl) {
        this.name = name;
        this.belongSite = belongSite;
        this.time = time;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return StringUtils.isTrimEmpty(name) ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBelongSite() {
        return StringUtils.isTrimEmpty(belongSite) ? "" : belongSite;
    }

    public void setBelongSite(String belongSite) {
        this.belongSite = belongSite;
    }

    public String getTime() {
        return StringUtils.isTrimEmpty(time) ? "" : time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

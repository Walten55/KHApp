package com.kehua.energy.monitor.app.model.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.NameInDb;

@Entity
@NameInDb("SGROUP_INFO")
public class SGroupInfo {
    @Id
    private long id;

    @NameInDb("PN")
    private String pn;

    @NameInDb("GROUP")
    private String group;

    @NameInDb("GROUP_NAME_CN")
    private String groupNameCN;

    @NameInDb("GROUP_NAME_EN")
    private String groupNameEN;

    @NameInDb("GROUP_NAME_FRENCH")
    private String groupNameFrench;

    @NameInDb("SGROUP")
    private String sgroup;

    @NameInDb("SGROUP_NAME_CN")
    private String sgroupNameCN;

    @NameInDb("SGROUP_NAME_EN")
    private String sgroupNameEN;

    @NameInDb("SGROUP_NAME_FRENCH")
    private String sgroupNameFrench;

    @NameInDb("AUTHORITY")
    private String authority;

    public SGroupInfo(){}

    public SGroupInfo(String pn, String group, String groupNameCN, String groupNameEN, String groupNameFrench, String sgroup, String sgroupNameCN, String sgroupNameEN, String sgroupNameFrench, String authority) {
        this.pn = pn;
        this.group = group;
        this.groupNameCN = groupNameCN;
        this.groupNameEN = groupNameEN;
        this.groupNameFrench = groupNameFrench;
        this.sgroup = sgroup;
        this.sgroupNameCN = sgroupNameCN;
        this.sgroupNameEN = sgroupNameEN;
        this.sgroupNameFrench = sgroupNameFrench;
        this.authority = authority;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroupNameCN() {
        return groupNameCN;
    }

    public void setGroupNameCN(String groupNameCN) {
        this.groupNameCN = groupNameCN;
    }

    public String getGroupNameEN() {
        return groupNameEN;
    }

    public void setGroupNameEN(String groupNameEN) {
        this.groupNameEN = groupNameEN;
    }

    public String getGroupNameFrench() {
        return groupNameFrench;
    }

    public void setGroupNameFrench(String groupNameFrench) {
        this.groupNameFrench = groupNameFrench;
    }

    public String getSgroup() {
        return sgroup;
    }

    public void setSgroup(String sgroup) {
        this.sgroup = sgroup;
    }

    public String getSgroupNameCN() {
        return sgroupNameCN;
    }

    public void setSgroupNameCN(String sgroupNameCN) {
        this.sgroupNameCN = sgroupNameCN;
    }

    public String getSgroupNameEN() {
        return sgroupNameEN;
    }

    public void setSgroupNameEN(String sgroupNameEN) {
        this.sgroupNameEN = sgroupNameEN;
    }

    public String getSgroupNameFrench() {
        return sgroupNameFrench;
    }

    public void setSgroupNameFrench(String sgroupNameFrench) {
        this.sgroupNameFrench = sgroupNameFrench;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}

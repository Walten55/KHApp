package com.kehua.energy.monitor.app.model.entity;

import com.kehua.energy.monitor.app.model.local.LocalModel;
import com.kehua.energy.monitor.app.utils.LanguageUtils;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.NameInDb;

@Entity
@NameInDb("GROUP_INFO")
public class GroupInfo {

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

    @NameInDb("DEVICE_TYPE")
    private int deviceType;

    public GroupInfo(){}

    public GroupInfo(String pn, String group, String groupNameCN, String groupNameEN, String groupNameFrench, int deviceType) {
        this.pn = pn;
        this.group = group;
        this.groupNameCN = groupNameCN;
        this.groupNameEN = groupNameEN;
        this.groupNameFrench = groupNameFrench;
        this.deviceType = deviceType;
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

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getGroupName() {
        LocalModel localModel = new LocalModel();
        String language = localModel.getLanguageSelect();

        String result = "";
        switch (language) {
            case LanguageUtils.Chinese:
                result = groupNameCN;
                break;
            case LanguageUtils.English:
                result = groupNameEN;
                break;
            default:
                result = groupNameCN;
                break;
        }
        return result;
    }
}

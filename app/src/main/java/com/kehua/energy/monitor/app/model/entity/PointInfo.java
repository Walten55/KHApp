package com.kehua.energy.monitor.app.model.entity;


import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.NameInDb;

@Entity
@NameInDb("POINT_INFO")
public class PointInfo {

    public PointInfo(){

    }

    @Id
    private long id;

    @NameInDb("PN")
    private String pn;

    @NameInDb("ADDRESS")
    private String address;

    @NameInDb("DESCRIPTION_CN")
    private String descriptionCN;

    @NameInDb("DESCRIPTION_EN")
    private String descriptionEN;

    @NameInDb("DESCRIPTION_FRENCH")
    private String descriptionFrench;

    @NameInDb("BYTE_COUNT")
    private int byteCount;

    /**
     * 0 boolean
     * 1 int
     * 2 double
     * 3 string
     */
    @NameInDb("DATA_TYPE")
    private String dataType;

    @NameInDb("ACCURACY")
    private int accuracy;

    @NameInDb("UNIT")
    private String unit;

    @NameInDb("DEVICE_TYPE")
    private int deviceType;

    @NameInDb("GROUP")
    private String group;

    @NameInDb("SGROUP")
    private String sgroup;

    @NameInDb("AUTHORITY")
    private String authority;


    public PointInfo(String pn, String address, String descriptionCN, String descriptionEN, String descriptionFrench, int byteCount, String dataType, int accuracy, String unit, int deviceType, String group, String sgroup, String authority) {
        this.pn = pn;
        this.address = address;
        this.descriptionCN = descriptionCN;
        this.descriptionEN = descriptionEN;
        this.descriptionFrench = descriptionFrench;
        this.byteCount = byteCount;
        this.dataType = dataType;
        this.accuracy = accuracy;
        this.unit = unit;
        this.deviceType = deviceType;
        this.group = group;
        this.sgroup = sgroup;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescriptionCN() {
        return descriptionCN;
    }

    public String getDescription() {
        return descriptionCN;
    }

    public void setDescriptionCN(String descriptionCN) {
        this.descriptionCN = descriptionCN;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    public void setDescriptionEN(String descriptionEN) {
        this.descriptionEN = descriptionEN;
    }

    public String getDescriptionFrench() {
        return descriptionFrench;
    }

    public void setDescriptionFrench(String descriptionFrench) {
        this.descriptionFrench = descriptionFrench;
    }

    public int getByteCount() {
        return byteCount;
    }

    public void setByteCount(int byteCount) {
        this.byteCount = byteCount;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSgroup() {
        return sgroup;
    }

    public void setSgroup(String sgroup) {
        this.sgroup = sgroup;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}

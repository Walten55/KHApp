package com.kehua.energy.monitor.app.model.entity;

import com.kehua.energy.monitor.app.model.local.LocalModel;
import com.kehua.energy.monitor.app.utils.LanguageUtils;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.NameInDb;

/**
 *
 */
@Entity
@NameInDb("DEVICE_DATA")
public class DeviceData {

    public static final int TYPE_STATUS = 1;
    public static final int TYPE_HOLD = 2;
    public static final int TYPE_SETTING = 3;

    public DeviceData() {
    }

    ;

    @Id
    private long id;

    /**
     * 设备号
     */
    @NameInDb("SN")
    private String sn;

    /**
     * 寄存器地址
     */
    @NameInDb("REGISTER_ADDRESS")
    private String registerAddress;

    /**
     * 1 状态量
     * 2 模拟量
     * 3 设置项
     */
    @NameInDb("TYPE")
    private int type;

    /**
     * 描述信息
     */
    @NameInDb("DESCRIPTION_CN")
    private String descriptionCN;

    @NameInDb("DESCRIPTION_EN")
    private String descriptionEN;

    @NameInDb("DESCRIPTION_FRENCH")
    private String descriptionFrench;

    /**
     * 解析前数据
     */
    @NameInDb("INT_VALUE")
    private int intValue;

    /**
     * 解析后数据
     */
    @NameInDb("PARSE_VALUE")
    private String parseValue;

    /**
     * 单位
     */
    @NameInDb("UNIT")
    private String unit;

    /**
     * 采集时间
     */
    @NameInDb("UPDATE_TIME")
    private Date updateTime;

    /**
     * 分组
     */
    @NameInDb("GROUP")
    private String group;

    /**
     * 排序
     */
    @NameInDb("SGROUP")
    private String sgroup;

    /**
     * 权限
     */
    @NameInDb("AUTHORITY")
    private String authority;

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

    @NameInDb("BYTE_COUNT")
    private int byteCount;

    public DeviceData(String sn, String registerAddress, int type, String descriptionCN, String descriptionEN, String descriptionFrench, int intValue, String parseValue, String unit, Date updateTime, String group, String sgroup, String authority, String dataType, int accuracy, int byteCount) {
        this.sn = sn;
        this.registerAddress = registerAddress;
        this.type = type;
        this.descriptionCN = descriptionCN;
        this.descriptionEN = descriptionEN;
        this.descriptionFrench = descriptionFrench;
        this.intValue = intValue;
        this.parseValue = parseValue;
        this.unit = unit;
        this.updateTime = updateTime;
        this.group = group;
        this.sgroup = sgroup;
        this.authority = authority;
        this.dataType = dataType;
        this.accuracy = accuracy;
        this.byteCount = byteCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        LocalModel localModel = new LocalModel();
        String language = localModel.getLanguageSelect();

        String result = "";
        switch (language) {
            case LanguageUtils.Chinese:
                result = descriptionCN;
                break;
            case LanguageUtils.English:
                result = descriptionEN;
                break;
            default:
                result = descriptionCN;
                break;
        }
        return result;
    }

    public String getDescriptionCN() {
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

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public String getParseValue() {
        return parseValue;
    }

    public void setParseValue(String parseValue) {
        this.parseValue = parseValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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

    public int getByteCount() {
        return byteCount;
    }

    public void setByteCount(int byteCount) {
        this.byteCount = byteCount;
    }
}

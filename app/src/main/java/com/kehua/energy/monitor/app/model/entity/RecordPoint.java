package com.kehua.energy.monitor.app.model.entity;


import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.NameInDb;

@Entity
@NameInDb("RECORD_POINT")
public class RecordPoint {

    public RecordPoint(){

    }

    @Id
    private long id;

    /**
     * 1 单相并脱网
     * 2 单相历史故障
     * 3 单相用户日志
     * 4 单相功率调度日志
     * 5 三相无屏并脱网、历史故障
     * 6 三相有屏并脱网、历史故障
     * 7 三相用户日志、功率调度日志
     */
    @NameInDb("TEMPLATE")
    private int template;

    @NameInDb("TEMPLATE_NAME")
    private String templateName;

    @NameInDb("CODE")
    private int code;

    @NameInDb("MEANS_CN")
    private String meansCN;

    @NameInDb("V0")
    private String v0;

    @NameInDb("V1")
    private String v1;

    @NameInDb("SIGN")
    private int sign;

    @NameInDb("UNIT")
    private String unit;

    @NameInDb("ACCURACY")
    private int accuracy;

    @NameInDb("V2")
    private String v2;

    @NameInDb("V3")
    private String v3;

    public RecordPoint(int template, String templateName, int code, String meansCN, String v0, String v1, int sign, String unit, int accuracy, String v2, String v3) {
        this.template = template;
        this.templateName = templateName;
        this.code = code;
        this.meansCN = meansCN;
        this.v0 = v0;
        this.v1 = v1;
        this.sign = sign;
        this.unit = unit;
        this.accuracy = accuracy;
        this.v2 = v2;
        this.v3 = v3;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTemplate() {
        return template;
    }

    public void setTemplate(int template) {
        this.template = template;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMeansCN() {
        return meansCN;
    }

    public void setMeansCN(String meansCN) {
        this.meansCN = meansCN;
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public String getV0() {
        return v0;
    }

    public void setV0(String v0) {
        this.v0 = v0;
    }

    public String getV1() {
        return v1;
    }

    public void setV1(String v1) {
        this.v1 = v1;
    }

    public String getV2() {
        return v2;
    }

    public void setV2(String v2) {
        this.v2 = v2;
    }

    public String getV3() {
        return v3;
    }

    public void setV3(String v3) {
        this.v3 = v3;
    }
}

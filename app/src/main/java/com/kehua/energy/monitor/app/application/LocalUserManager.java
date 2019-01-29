package com.kehua.energy.monitor.app.application;

public class LocalUserManager {
    //普通人员
    public static final int ROLE_NORMAL = 1;
    //运维人员
    public static final int ROLE_OPS = 2;
    //厂家人员
    public static final int ROLE_FACTORY = 3;

    public static final String OPS_PASSWORD = "BCBE3365E6AC95EA2C0343A2395834DD";

    public static boolean IN_THE_UPGRADE = false;

    //角色权限
    private static String roleAuthority;

    private static int role;

    //协议号
    private static int pn;

    private static int deviceAddress=0x01;

    private static int deviceType;

    private static String sn = "";

    public static String getSn() {
        return sn;
    }

    public static void setSn(String sn) {
        LocalUserManager.sn = sn;
    }

    public static void setRole(int role) {
        LocalUserManager.role = role;
    }

    public static int getRole() {
        return role;
    }

    public static String getRoleAuthority() {
        switch (role) {
            case ROLE_NORMAL:
                return "normal";
            case ROLE_OPS:
                return "ops";
            case ROLE_FACTORY:
                return "factory";
            default:

                return "normal";
        }
    }

    public static void setPn(int pn) {
        LocalUserManager.pn = pn;
    }

    public static int getPn(){
        return LocalUserManager.pn;
    }

    public static int getDeviceAddress() {
        return deviceAddress;
    }

    public static void setDeviceAddress(int deviceAddress) {
        LocalUserManager.deviceAddress = deviceAddress;
    }

    public static int getDeviceType() {
        return deviceType;
    }

    public static void setDeviceType(int deviceType) {
        LocalUserManager.deviceType = deviceType;
    }
}

package com.kehua.energy.monitor.app.route;


import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by walten on 2018/4/25.
 */
public class RouterMgr {

    /**
     * 采集器联网
     */
    public static final int TYPE_SETTING = 1;
    /**
     * 本地模式
     */
    public static final int TYPE_OFF_NETWORK = 2;

    public static final String HOTSPOT="/business/hotspot";
    public static final String WIFI_CONFIG="/business/wifiConfig";
    public static final String LOGIN="/business/login";
    public static final String REGISTER="/business/register";
    public static final String FORGOT_PWD="/business/forgetPassword";
    public static final String HOME = "/business/home";
    public static final String FAVORITE = "/business/favorite";
    public static final String ALARM_LIST = "/business/alarmList";


    public static final String LOCAL_MAIN="/business/local/main";
    public static final String LOCAL_MONITOR = "/business/local/monitor";
    public static final String LOCAL_HISTORY = "/business/local/history";
    public static final String LOCAL_HISTORY_INFO = "/business/local/historyInfo";
    public static final String LOCAL_ALARM = "/business/local/alarm";
    public static final String LOCAL_SETTING = "/business/local/setting";
    public static final String LOCAL_LOGIN = "/business/local/login";
    public static final String LOCAL_SCAN = "/business/local/scan";
    public static final String LOCAL_ABOUT_SN= "/business/local/aboutSN";
    public static final String LOCAL_UPGRADE= "/business/local/upgrade";
    public static final String LOCAL_DATA = "/business/local/data";

    public static final String LOCAL_SETTING_BASIC = "/business/local/setting/basic";
    public static final String LOCAL_SETTING_ADVANCED = "/business/local/setting/advanced";
    public static final String LOCAL_SETTING_BATTERY = "/business/local/setting/battery";
    public static final String LOCAL_SETTING_GRID = "/business/local/setting/grid";
    public static final String LOCAL_SETTING_PATTERN = "/business/local/setting/pattern";
    public static final String LOCAL_SETTING_CALIBRATION = "/business/local/setting/calibration";
    public static final String LOCAL_SETTING_DEVICE = "/business/local/setting/device";
    public static final String LOCAL_SETTING_STANDARD = "/business/local/setting/standard";

    public static final String LOCAL_SETTING_PATTERN_CHILD = "/business/local/setting/pattern/child";

    public static final String PERSONAL = "/business/personal";
    public static final String LANGUAGE = "/business/language";
    public static final String ABOUT = "/business/about";


    private List<String> routerPathNeedLogin;

    private RouterMgr() {
        routerPathNeedLogin = new ArrayList<>();

    }

    public static RouterMgr get(){
        return new RouterMgr();
    }

    public List<String> getRouterPathNeedLogin() {
        return routerPathNeedLogin;
    }

    /**
     * 热点列表
     * @param type
     */
    public void hotspot(int type) {
        ARouter.getInstance()
                .build(HOTSPOT)
                .withInt("type", type)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * WIFI 配置
     */
    public void wifiConfig() {
        ARouter.getInstance()
                .build(WIFI_CONFIG)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 登陆
     */
    public void login() {
        ARouter.getInstance()
                .build(LOGIN)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 注册
     */
    public void register() {
        ARouter.getInstance()
                .build(REGISTER)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * FORGOT_PWD
     */
    public void forgetPassword() {
        ARouter.getInstance()
                .build(FORGOT_PWD)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 本地模式主页
     */
    public void localMain() {
        ARouter.getInstance()
                .build(LOCAL_MAIN)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 本地模式角色页面(登录)
     */
    public void localLogin() {
        ARouter.getInstance()
                .build(LOCAL_LOGIN)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 串号相关设置
     */
    public void localAboutSn() {
        ARouter.getInstance()
                .build(LOCAL_ABOUT_SN)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 设备升级
     */
    public void localUpgrade() {
        ARouter.getInstance()
                .build(LOCAL_UPGRADE)
                .navigation(ActivityUtils.getTopActivity());
    }



    /**
     * 本地模式角色页面(登录)
     */
    public void localSettingStandard() {
        ARouter.getInstance()
                .build(LOCAL_SETTING_STANDARD)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 本地模式—模式设置- 子页面
     */
    public void localSettingPatternChild(String sGroup) {
        ARouter.getInstance()
                .build(LOCAL_SETTING_PATTERN_CHILD)
                .withString("sGroup",sGroup)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 语言
     */
    public void language() {
        ARouter.getInstance()
                .build(LANGUAGE)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 关于
     */
    public void about() {
        ARouter.getInstance()
                .build(ABOUT)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     *
     * @param count
     * @param index
     * @param recordType
     * @param template
     */
    public void localHistoryInfo(int count,int index,int recordType,int template) {
        ARouter.getInstance()
                .build(LOCAL_HISTORY_INFO)
                .withInt("count",count)
                .withInt("index",index)
                .withInt("recordType",recordType)
                .withInt("template",template)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 扫描
     */
    public void scan() {
        ARouter.getInstance()
                .build(LOCAL_SCAN)
                .navigation(ActivityUtils.getTopActivity());
    }

}

package com.kehua.energy.monitor.app.configuration;

public class Config {

    public static final String COLLECTOR_ADDRESS = "http://10.10.10.1/";

    public static final int PASSWORD_LENGTH_MIN = 6;

    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 验证码等待时长（单位：s）
     */
    public static final int WAIT_SECONDS = 10;

    /**
     * 以 MobSDK.gradle为准
     */
    public static final int SHARE_QQ_ID = 2;
    public static final int SHARE_QQ_SORT_ID = 2;

    public static final int SHARE_WECHAT_ID = 4;
    public static final int SHARE_WECHAT_SORT_ID = 4;

    /**
     * 本地模式采集成功
     */
    public static final String EVENT_CODE_COLLECT_COMPLETE = "event_code_collect_complete";
    public static final String EVENT_CODE_PROBATION_EXPIRE = "event_code_probation_expire";
    public static final String EVENT_CODE_PROBATION_NEAR = "event_code_probation_near";
    public static final String EVENT_CODE_PROBATION_NORMAL = "event_code_probation_normal";
    public static final String EVENT_CODE_LOCK = "event_code_lock";
    public static final String EVENT_CODE_COLLECT_SETTING_COMPLETE = "event_code_collect_setting_complete";
    public static final String EVENT_CODE_COLLECT_HISTORY_COMPLETE = "event_code_collect_history_complete";
    public static final String EVENT_CODE_STANDARD_CHOOSED = "event_code_standard_choosed";
    public static final String EVENT_CODE_BRANCH_SET = "event_code_branch_set";
    public static final String EVENT_CODE_SCAN_RESULT = "event_code_scan_result";
    public static final int REFRESH_DELAY = 1000;

    public static final String EVENT_CODE_FROGET_PHONECODE = "event_code_froget_phonecode";
    public static final String EVENT_CODE_NEW_PASSWORD = "event_code_new_password";
}

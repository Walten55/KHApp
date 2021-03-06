package com.kehua.energy.monitor.app.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.StringUtils;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.model.local.LocalModel;

import java.util.Locale;

/**
 * 用于本机语言切换所需的常服你操作封装
 */

public class LanguageUtils {
    private final static String LanguageSPKey = "LanguageSPKey";

    public final static String Chinese = "zh";
    public final static String English = "en";

    private static boolean forceUpdate = true;

    private static Locale targetLable;

    /**
     * @des:App初始化时候进行语言设置
     */
    public static void init(Context context) {
        String defaultLanguage = getSysDefaultLanguage(context);
        String spSelectedLanguage = LocalModel.getLanguageSelect();
        //如果没有设置,设置即可
        if (StringUtils.isTrimEmpty(spSelectedLanguage)) {
            LocalModel.saveLanguageSelect(defaultLanguage);
        } else {
            //查看默认的与设置的
            if (!defaultLanguage.equals(spSelectedLanguage) && forceUpdate) {
                forceUpdate = false;
                languageSelect(context, spSelectedLanguage, true);
            }
        }

    }

    /*
     * 获取系统默认的语言类型
     *
     * */
    public static String getSysDefaultLanguage(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        Locale locale = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? configuration.getLocales().get(0)
                : configuration.locale;
        String language = locale.getLanguage();
        //判断语言(默认返回中文)
        if (language.endsWith(English)) {
            return English;
        }
        return Chinese;
    }

    /**
     * @des:获取sharedPreferences 获取语言
     */
    public static String[] getLanguageNames(Context context) {
        return new String[]{
                context.getString(R.string.中文),
                context.getString(R.string.英文)
        };
    }

    /**
     * @des:进行语言切换
     */
    public static void languageSelect(Context context, String language, boolean forceUpdate) {
        //强制转换就直接切换，否则若与本地存储的不一致就进行切换
        if (forceUpdate || !language.equals(LocalModel.getLanguageSelect())) {
            LocalModel.saveLanguageSelect(language);

            Resources resources = context.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            targetLable = Locale.getDefault();

            //切换成中文
            if (Chinese.equals(language)) {
                targetLable = Locale.CHINESE;
            } else if (English.equals(language)) {
                targetLable = Locale.ENGLISH;
            }

            //设置，根据sdk 版本进行设置（7.0以上设置方式不同）
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LanguageUtils.wrap(context, targetLable);
            } else {
                configuration.locale = targetLable;
                configuration.setLayoutDirection(targetLable);
                resources.updateConfiguration(configuration, dm);
            }
            if (!forceUpdate) {
                ActivityUtils.getTopActivity().recreate();
            }
        }
    }


    /**
     * @des:根据名称获取对应键值
     */
    public static String getLangeValueByName(Context context, String name) {
        if (StringUtils.isTrimEmpty(name)) {
            return "";
        }
        //中文
        if (context.getResources().getString(R.string.中文).equals(name)) {
            return Chinese;
        } else if (context.getResources().getString(R.string.英文).equals(name)) {
            return English;
        }
        return "";
    }


    public static ContextWrapper wrap(Context context, Locale newLocale) {
        Resources res = context.getResources();
        Configuration configuration = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale);
            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            context = context.createConfigurationContext(configuration);
        } else {
            configuration.setLocale(newLocale);
            context = context.createConfigurationContext(configuration);
        }
        res.updateConfiguration(configuration,res.getDisplayMetrics());

        return new ContextWrapper(context);
    }

    public static Locale getTargetLable() {
        targetLable = targetLable == null ? Locale.getDefault() : targetLable;
        return targetLable;
    }
}

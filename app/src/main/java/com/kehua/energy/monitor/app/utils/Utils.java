package com.kehua.energy.monitor.app.utils;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/*
 * -----------------------------------------------------------------
 * Copyright by 2018 Walten, All rights reserved.
 * -----------------------------------------------------------------
 * desc:
 * -----------------------------------------------------------------
 * 2018/10/8 : Create Utils.java (Walten);
 * -----------------------------------------------------------------
 */public class Utils {

     public static String parseAccuracy(int result,int accuracy){
         String parseV;
         if (accuracy == 0) {
             parseV = String.valueOf(result);
         } else {
             String pattern = "#0.";
             for (int i = 0; i < accuracy; i++) {
                 pattern += "0";
             }
             DecimalFormat format = new DecimalFormat(pattern);
             parseV = format.format(result / Math.pow(10, accuracy));
         }

         return parseV;
     }

    public static String startWithZero(int value){
        String tempStr = value < 10 ? "0" + value : "" + value;
        return tempStr;
    }

    /*
     * 是否为s？double或float类型。
     * @param str 传入的字符串。
     * @return 是浮点数返回true,否则返回false。
     */
    public static boolean isNum(String str) {
        Pattern intPattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Pattern dfPattern = Pattern.compile("^[-\\+]?[.\\d]*$");

        return (intPattern.matcher(str).matches()||dfPattern.matcher(str).matches());
    }


}

package com.kehua.energy.monitor.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * 判断多个时间段是否出现重叠
 */
public class TimeSlotUtils {
    private static SimpleDateFormat ymdFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat ymdhmsFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static boolean checkOverlap(List<String> list){
        Collections.sort(list);//排序ASC

        boolean flag = false;//是否重叠标识
        for(int i=0; i<list.size(); i++){
            if(i>0){
                //跳过第一个时间段不做判断
                String[] itime = list.get(i).split("-");
                for(int j=0; j<list.size(); j++){
                    //如果当前遍历的i开始时间小于j中某个时间段的结束时间那么则有重叠，反之没有重叠
                    //这里比较时需要排除i本身以及i之后的时间段，因为已经排序了所以只比较自己之前(不包括自己)的时间段
                    if(j==i || j>i){
                        continue;
                    }

                    String[] jtime = list.get(j).split("-");
                    //此处DateUtils.compare为日期比较(返回负数date1小、返回0两数相等、返回正整数date1大)

                    String iTimeStr = ymdFormatter.format(new Date())+" "+itime[0]+":00";
                    String jTimeStr = ymdFormatter.format(new Date())+" "+jtime[1]+":00";

                    int compare = -1;
                    try {
                        compare = ymdhmsFormatter.parse(iTimeStr).compareTo(ymdhmsFormatter.parse(jTimeStr));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(compare<0){
                        flag = true;
                        break;//只要存在一个重叠则可退出内循环
                    }
                }
            }

            //当标识已经认为重叠了则可退出外循环
            if(flag){
                break;
            }
        }

        return flag;
    }

//    public static void main(String[] args) {
//        List<String> list = new ArrayList<String>();
//        list.add("08:00-09:00");
//        list.add("09:00-12:00");
//        list.add("13:00-16:30");
//        list.add("16:00-17:00");
//        list.add("18:00-20:00");
//
//        boolean flag = checkOverlap(list);
//        for(String time : list){
//            System.out.println(time);
//        }
//
//        System.out.println("\n当前时间段列表重叠验证结果为：" + flag);
//    }
}
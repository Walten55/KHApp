package com.kehua.energy.monitor.app.configuration;

import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.model.entity.Standard;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.walten.fastgo.common.Fastgo;

public class Frame {
    
    /**
     * 信息分类
     */
    public static final String 普通告警 = "01";
    public static final String 内部告警 = "02";
    public static final String 设备状态 = "03";
    public static final String 设备信息 = "04";
    public static final String 电网信息 = "05";
    public static final String 负载信息 = "06";
    public static final String 光伏信息 = "07";
    public static final String 电池信息 = "08";
    public static final String 内部信息 = "09";

    public static final String 基本设置 = "10";
    public static final String 高级设置 = "11";
    public static final String 电网设置 = "13";
    public static final String 电池设置 = "16";
    public static final String 模式设置 = "17";
    public static final String 校准设置 = "18";
    public static final String 设备设置 = "19";

    public static final int 自用优先 = 0;
    public static final int 储能优先 = 1;
    public static final int 削峰填谷 = 2;
    public static final int 能量调度 = 3;

    public static final int 待机 = 1;
    public static final int 并网 = 2;
    public static final int 故障 = 4;
    public static final int 关机 = 0;
    public static final int 离网 = 3;

    public static final int 三相光伏逆变器 = 0x01;
    public static final int 三相光伏储能逆变器 = 0x02;
    public static final int 单相光伏并网逆变器 = 0x0A;
    public static final int 单相光伏储能变流器 = 0x0B;

    public static final int 单相协议 = 2;
    public static final int 三相协议 = 1;
    public static final int 通用协议 = 3;

    public static final int 光伏光储 = 1;
    public static final int 光储 = 2;

    public static final int ON = 0xff00;
    public static final int OFF = 0x0000;

    public static final int 恢复出厂设置地址(){
        return 单相协议 == LocalUserManager.getPn()?5017:5006;
    }
    public static final int 清除故障录波地址(){
        return 单相协议 == LocalUserManager.getPn()?5077:5073;
    }
    public static final int 清除拉弧故障地址(){
        return 单相协议 == LocalUserManager.getPn()?9999:5008;
    }
    public static final int 清除所有发电量地址 = 5064;
    public static final int 清除历史记录地址 = 5065;
    public static final int[] 开机密码地址 = {6602,6603};
    public static final int[] 试用期密码地址 = {6604,6605};
    public static final int 开机密码功能地址 = 6810;
    public static final int 试用期功能地址 = 6811;

    private static final int 独立 = 0;
    private static final int 四路并联 = 1;
    private static final int 两路并联 = 2;

    public static final int 并脱网记录 = 1;
    public static final int 历史故障 = 2;
    public static final int 用户日志 = 3;
    public static final int 功率调度 = 4;

    public static int 标准类型地址(){
        return 单相协议 == LocalUserManager.getPn()?6300:6304;
    }

    /**
     * 分帧 单相
     */
    public static final int[] 单相_状态量_采集1 = {2501,2628};
    public static final int[] 单相_状态量_采集2 = {3500,3516};

    public static final int[] 单相_模拟量_采集1 = {4501,4565};
    public static final int[] 单相_模拟量_采集2 = {4571,4605};
    public static final int[] 单相_模拟量_采集3 = {4850,4874};
    public static final int[] 单相_模拟量_采集4 = {4901,4925};
    public static final int[] 单相_模拟量_采集5 = {5500,5506};
    public static final int[] 单相_模拟量_采集6 = {5600,5659};
    public static final int[] 单相_模拟量_采集7 = {5850,5851};

    /**
     * 分帧 三相
     */
    public static final int[] 三相_状态量_采集1 = {2501,2660};
    public static final int[] 三相_状态量_采集2 = {2661,2822};
    public static final int[] 三相_状态量_采集3 = {3500,3527};

    public static final int[] 三相_模拟量_采集1 = {4501,4610};
//    public static final int[] 三相_模拟量_采集2 = {4611,4676};
    public static final int[] 三相_模拟量_采集2 = {4651,4676};
    public static final int[] 三相_模拟量_采集3 = {4701,4796};
    public static final int[] 三相_模拟量_采集4 = {4800,4874};
    public static final int[] 三相_模拟量_采集5 = {5500,5512};
    public static final int[] 三相_模拟量_采集6 = {5600,5645};
    public static final int[] 三相_模拟量_采集7 = {5850,5851};

    /**
     * 基本设置采集
     */
    public static final int 开关机地址 = 5000;
    public static final int 工作模式地址 = 6200;
    public static final int 充电时段数地址 = 6026;
    public static final int 放电时段数地址 = 6039;
    public static final int[] 充放电时段采集 = {6026,6051};

    /**
     * 设备设置采集
     */
    public static final int 串号相关串号地址 = 6800;
    public static final int 串号相关开机密码功能地址 = 6810;
    public static final int 串号相关试用期功能地址 = 6811;
    public static final int 串号相关试用期天数地址 = 6812;
    public static final int MAC地址 = 6813;
    public static final int 机器型号设置地址 = 6816;
    public static int 机器型号地址(){
        return 单相协议 == LocalUserManager.getPn()?4571:4800;
    }
    public static final int 站号配置串号地址 = 7000;
    public static final int 站号配置站号地址 = 7010;

    /**
     * 本地模式设置中部分类别
     */
    public static final int[] 本地模式设置_模拟量_采集 = {6426,6486};
    public static final int L_HVRT模式地址 = 6426;
    public static final int L_HFRT模式地址 = 6437;

    public static final int P_V模式模式地址 = 6446;
    public static final int P_V模式放电起始地址 = 6447;
    public static final int P_V模式放电结束地址 = 6452;
    public static final int P_V模式充电起始地址 = 6453;
    public static final int P_V模式充电结束地址 = 6458;

    public static final int P_F模式模式地址 = 6459;
    public static final int P_F模式放电起始地址 = 6460;
    public static final int P_F模式放电结束地址 = 6465;
    public static final int P_F模式充电起始地址 = 6466;
    public static final int P_F模式充电结束地址 = 6471;

    public static final int Q_V模式地址 = 6472;
    public static final int Q_V模式V1地址 = 6473;
    public static final int SPF模式 = 6480;

    /**
     * 串号
     */
    public static final int[] 单相串号采集 = {4596,4605};
    public static final int[] 三相串号采集 = {4825,4834};
    /**
     * 设备类型
     */
    public static final int[] 设备类型采集 = {4850,4850};
    /**
     * 协议类型
     */
    public static final int[] 协议类型采集 = {4852,4852};

    public static final int 运行状态地址 = 4501;

    public static final int 支路告警屏蔽地址 = 6305;
    public static final int PV支路使能字地址 = 6309;

    public static final int MPPT路数地址 = 4851;
    public static final int PV支路数地址 = 4873;

    public static int 电池日充电量地址(){
        return 单相协议 == LocalUserManager.getPn()?4532:4560;
    }
    public static int 电池日放电量地址(){
        return 单相协议 == LocalUserManager.getPn()?4533:4561;
    }

    public static int 日负载耗电量地址(){
        return 单相协议 == LocalUserManager.getPn()?4515:4623;
    }

    public static int[] 试用期到期临近采集(){
        return 单相协议 == LocalUserManager.getPn()?new int[]{
                2539,2540
        }:new int[]{
                2531,2532
        };
    }
    public static int 机器锁定状态地址(){
        return 单相协议 == LocalUserManager.getPn()?2597:2821;
    }
    public static int 试用状态地址(){
        return 单相协议 == LocalUserManager.getPn()?2598:2822;
    }

    public static int[] 总负载耗电量地址(){
        return 单相协议 == LocalUserManager.getPn()?new int[]{
                4551,4552
        }:new int[]{
                4624,4625
        };
    }
    public static int[] 总并网用电量地址(){
        return 单相协议 == LocalUserManager.getPn()?new int[]{
                4553,4554
        }:new int[]{
                4530,4531
        };
    }

    public static int 日并网发电量地址 = 4506;
    public static int[] 总并网发电量地址(){
        return 单相协议 == LocalUserManager.getPn()?new int[]{
                4538,4539
        }:new int[]{
                4507,4508
        };
    }

    public static int PV日发电量地址(){
        return 单相协议 == LocalUserManager.getPn()?4526:4651;
    }
    public static int[] PV总发电量地址(){
        return 单相协议 == LocalUserManager.getPn()?new int[]{
                4540,4541
        }:new int[]{
                4652,4653
        };
    }

    public static int 电池类型地址(){
        return 单相协议 == LocalUserManager.getPn()?6308:6350;
    }

    public static int 充电倍率地址(){
        return 单相协议 == LocalUserManager.getPn()?6312:6354;
    }

    public static int 控制软件1地址(){
        return 单相协议 == LocalUserManager.getPn()?4586:4840;
    }
    public static int 控制软件2地址(){
        return 单相协议 == LocalUserManager.getPn()?4591:4845;
    }
    public static int 电池状态地址(){
        return 单相协议 == LocalUserManager.getPn()?4527:4551;
    }

    public static int PV总功率地址(){
        return 单相协议 == LocalUserManager.getPn()?4537:4527;
    }
    public static int 并网有功功率地址(){
        return 单相协议 == LocalUserManager.getPn()?4508:4518;
    }
    public static int 并网无功功率地址(){
        return 单相协议 == LocalUserManager.getPn()?4509:4519;
    }

    public static int 电池充电功率地址(){
        //三相没有
        return 单相协议 == LocalUserManager.getPn()?4536:9999;
    }
    public static int 电池放电功率地址(){
        //三相没有
        return 单相协议 == LocalUserManager.getPn()?4535:9999;
    }
    public static int 负载有功功率地址(){
        return 单相协议 == LocalUserManager.getPn()?4517:4607;
    }
    public static int 次要负载有功功率地址(){
        //三相没有
        return 单相协议 == LocalUserManager.getPn()?4557:9999;
    }

    public static boolean isStorageDevice(int type){
        return type == 0x02 || type == 0x0B;
    }

    public static String getBatStatusName(int type){
        switch (type){
            case 0:

                return Fastgo.getContext().getString(R.string.空闲);
            case 1:

                return Fastgo.getContext().getString(R.string.充电);
            case 2:

                return Fastgo.getContext().getString(R.string.放电);
            case 3:

                return Fastgo.getContext().getString(R.string.异常);
        }
        return "--";
    }

    public static String getDeviceTypeName(int type){
        Locale locale=Locale.getDefault();
        switch (type){
            case 三相光伏逆变器:

                return Fastgo.getContext().getString(R.string.三相光伏逆变器);
            case 三相光伏储能逆变器:

                return Fastgo.getContext().getString(R.string.三相光伏储能逆变器);
            case 单相光伏并网逆变器:

                return Fastgo.getContext().getString(R.string.单相光伏并网逆变器);
            case 单相光伏储能变流器:

                return Fastgo.getContext().getString(R.string.单相光伏储能变流器);
        }
        return "--";
    }

    public static String getProtocolTypeName(int type){
        
        switch (type){
            case 单相协议:

                return Fastgo.getContext().getString(R.string.单相协议);
            case 三相协议:

                return Fastgo.getContext().getString(R.string.三相协议);
        }
        return "--";
    }

    public static String getDeviceRunningState(int state){
        
        //0-关机；1-待机；2-并网；3-离网；4-故障 单相
        //0-待机1-并网 2-故障 3-关机 4-离网 三相
        if(LocalUserManager.getPn() == Frame.单相协议){
            switch (state){
                case 1:

                    return Fastgo.getContext().getString(R.string.待机);
                case 2:

                    return Fastgo.getContext().getString(R.string.并网);
                case 4:

                    return Fastgo.getContext().getString(R.string.故障);
                case 0:

                    return Fastgo.getContext().getString(R.string.关机);

                case 3:

                    return Fastgo.getContext().getString(R.string.离网);
            }
        }else {
            switch (state){
                case 0:

                    return Fastgo.getContext().getString(R.string.待机);
                case 1:

                    return Fastgo.getContext().getString(R.string.并网);
                case 2:

                    return Fastgo.getContext().getString(R.string.故障);
                case 3:

                    return Fastgo.getContext().getString(R.string.关机);

                case 4:

                    return Fastgo.getContext().getString(R.string.离网);
            }
        }

        return "";
    }

    public static String getWorkPatternName(int value){
        switch (value){
            case 自用优先:

                return Fastgo.getContext().getString(R.string.自用优先);
            case 储能优先:

                return Fastgo.getContext().getString(R.string.储能优先);
            case 削峰填谷:

                return Fastgo.getContext().getString(R.string.削峰填谷);
            case 能量调度:

                return Fastgo.getContext().getString(R.string.能量调度);

        }
        return "";
    }

    public static String getMPPTShunt(int value){
        switch (value){
            case 独立:

                return Fastgo.getContext().getString(R.string.独立);
            case 四路并联:

                return Fastgo.getContext().getString(R.string.四路并联);
            case 两路并联:

                return Fastgo.getContext().getString(R.string.两路并联);

        }
        return "";
    }

    public static String getReferenceModel(int value){
        switch (value){
            case 0:

                return "SPI50K-B";
            case 1:

                return "SPI60K-B";
            case 2:

                return "SPI50K-BHV";

            case 3:

                return "SPI60K-BHV";

            case 4:

                return "SPI70K-BHV";

            case 5:
                return "SPI80K-BHV";

        }
        return "";
    }

    public static String getBatteryType(int value){
        switch (value){
            case 0:

                return Fastgo.getContext().getString(R.string.铅酸电池);
            case 1:

                return Fastgo.getContext().getString(R.string.磷酸铁锂电池);
            case 2:

                return Fastgo.getContext().getString(R.string.三元电池);

            case 3:

                return Fastgo.getContext().getString(R.string.铅碳电池);
        }
        return "";
    }

    public static String getChargeRate(int value){
        switch (value){
            case 0:

                return "0.1C";
            case 1:

                return "0.2C";
            case 2:

                return "0.5C";
            case 3:

                return "1C";

        }
        return "";
    }

    public static String getToggleName(int value){
        switch (value){
            case 0:

                return Fastgo.getContext().getString(R.string.关闭);
            case 1:

                return Fastgo.getContext().getString(R.string.开启);
        }
        return "";
    }

    public static String getCXNT(int value){
        switch (value){
            case 0:

                return Fastgo.getContext().getString(R.string.无);
            case 1:

                return Fastgo.getContext().getString(R.string.CT);
            case 2:

                return Fastgo.getContext().getString(R.string.智能电表);

        }
        return "";
    }

    public static String getCT(int value){
        switch (value){
            case 0:

                return "0-75/5";
            case 1:

                return "1-50/5";
            case 2:

                return "2-40/5";
            case 3:

                return "3-30/5";
            case 4:

                return "4-25/5";
            case 5:

                return "5-20/5";

        }
        return "";
    }


    public static List<Standard> getStandardList(){
        List<Standard> data = new ArrayList<>();

        if (LocalUserManager.getPn() == Frame.单相协议) {
            data.add(new Standard(0, Fastgo.getContext().getString(R.string.德国), R.mipmap.img_country_germany));
            data.add(new Standard(1, Fastgo.getContext().getString(R.string.英国), R.mipmap.img_country_britain));
            data.add(new Standard(2, Fastgo.getContext().getString(R.string.中国), R.mipmap.img_country_china));
            data.add(new Standard(3, Fastgo.getContext().getString(R.string.澳洲), R.mipmap.img_country_australia));
            data.add(new Standard(4, Fastgo.getContext().getString(R.string.新西兰), R.mipmap.img_country_new_zealand));
            data.add(new Standard(5, Fastgo.getContext().getString(R.string.法国VDE), R.mipmap.img_country_france));
            data.add(new Standard(6, Fastgo.getContext().getString(R.string.意大利), R.mipmap.img_country_italy));
            data.add(new Standard(7, Fastgo.getContext().getString(R.string.荷兰), R.mipmap.img_country_holland));
            data.add(new Standard(8, Fastgo.getContext().getString(R.string.西班牙), R.mipmap.img_country_spain));
            data.add(new Standard(9, Fastgo.getContext().getString(R.string.泰国PEA), R.mipmap.img_country_thailand));
            data.add(new Standard(10, Fastgo.getContext().getString(R.string.泰国MEA), R.mipmap.img_country_thailand));
            data.add(new Standard(11, Fastgo.getContext().getString(R.string.美国), R.mipmap.img_country_america));
            data.add(new Standard(12, Fastgo.getContext().getString(R.string.美国加州), R.mipmap.img_country_america));
            data.add(new Standard(13, Fastgo.getContext().getString(R.string.加拿大), R.mipmap.img_country_canada));
            data.add(new Standard(14, Fastgo.getContext().getString(R.string.农村电网), R.mipmap.img_standard_rural));
            data.add(new Standard(15,Fastgo.getContext() .getString(R.string.城镇电网), R.mipmap.img_standard_town));
            data.add(new Standard(16, Fastgo.getContext().getString(R.string.法国VFR), R.mipmap.img_country_france));
            data.add(new Standard(17, Fastgo.getContext().getString(R.string.法国SEI), R.mipmap.img_country_france));
            data.add(new Standard(18, Fastgo.getContext().getString(R.string.法国CRAE), R.mipmap.img_country_france));
            data.add(new Standard(19, Fastgo.getContext().getString(R.string.定制1), R.mipmap.img_standard_customized));
            data.add(new Standard(20, Fastgo.getContext().getString(R.string.定制2), R.mipmap.img_standard_customized));
            data.add(new Standard(21, Fastgo.getContext().getString(R.string.定制3), R.mipmap.img_standard_customized));
            data.add(new Standard(22, Fastgo.getContext().getString(R.string.定制4), R.mipmap.img_standard_customized));
            data.add(new Standard(23, Fastgo.getContext().getString(R.string.定制5), R.mipmap.img_standard_customized));
        } else {
            data.add(new Standard(0, Fastgo.getContext().getString(R.string.中国), R.mipmap.img_country_china));
            data.add(new Standard(1, Fastgo.getContext().getString(R.string.美国), R.mipmap.img_country_america));
            data.add(new Standard(2, Fastgo.getContext().getString(R.string.美国加州), R.mipmap.img_country_america));
            data.add(new Standard(3, Fastgo.getContext().getString(R.string.德国), R.mipmap.img_country_germany));
            data.add(new Standard(4, Fastgo.getContext().getString(R.string.澳洲), R.mipmap.img_country_australia));
            data.add(new Standard(5, Fastgo.getContext().getString(R.string.新西兰), R.mipmap.img_country_new_zealand));
            data.add(new Standard(6, Fastgo.getContext().getString(R.string.英国), R.mipmap.img_country_britain));
            data.add(new Standard(7, Fastgo.getContext().getString(R.string.泰国PEA), R.mipmap.img_country_thailand));
            data.add(new Standard(8, Fastgo.getContext().getString(R.string.泰国MEA), R.mipmap.img_country_thailand));
            data.add(new Standard(9, Fastgo.getContext().getString(R.string.意大利), R.mipmap.img_country_italy));
            data.add(new Standard(10, Fastgo.getContext().getString(R.string.法国), R.mipmap.img_country_france));
            data.add(new Standard(11, Fastgo.getContext().getString(R.string.加拿大), R.mipmap.img_country_canada));
            data.add(new Standard(12, Fastgo.getContext().getString(R.string.其他), R.mipmap.img_standard_other));
        }

        return data;
    }
}

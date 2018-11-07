package com.kehua.energy.monitor.app.model.entity;

import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.utils.ByteUtils;
import com.kehua.energy.monitor.app.utils.Crc16Utils;

import java.util.List;

/**
 * 指令
 */
public class Cmd {
    private int devAddr;
    private int fc;
    private int start;
    private int len;

    private Cmd(){

    }

    private Cmd(int devAddr, int fc, int start, int len){
        this.devAddr = devAddr;
        this.fc = fc;
        this.start = start;
        this.len = len;
    }

    private Cmd(int devAddr, int fc, int start){
        this.devAddr = devAddr;
        this.fc = fc;
        this.start = start;
        this.len = 1;
    }

    public static String newReadCmd(int devAddr, int fc, int start, int end){
        return new Cmd(devAddr,fc,start,end+1-start).toHexString();
    }

    public static String newWriteCmd(int devAddr,int start,boolean value){
        return new Cmd(devAddr, 0x05, start).toHexStringWithBoolean(value);
    }

    public static String newWriteCmd(int devAddr,int start,int value){
        return new Cmd(devAddr, 0x06, start).toHexStringWithInt(value);
    }

    public static String newWriteCmd(int devAddr,int start, int end,String value){
        return new Cmd(devAddr,0x10,start,end+1-start).toHexStringWithString(value);
    }

    public static String newWriteCmd(int devAddr,int start, int end,int value){
        return new Cmd(devAddr,0x10,start,end+1-start).toHexStringWithMoreAddressInt(value);
    }

    public static String newWriteCmd(int devAddr,int start, int end,int[] values){
        return new Cmd(devAddr,0x10,start,end+1-start).toHexStringWithMoreAddressInts(values);
    }

    public static String newTimeFrameCmd(int devAddr,List<Integer> chargeValues,List<Integer> dischargeValues){
        Cmd cmd = new Cmd(devAddr,0x10, Frame.充电时段数地址,6051+1-Frame.充电时段数地址);

        return cmd.toTimeFrameCmdHexString(chargeValues,dischargeValues);
    }

    public static String newPowerOnAndProbationPeriodPwdCmd(int devAddr,int powerOnPwd,int probationPeriodPwd){
        Cmd cmd = new Cmd(devAddr,0x10, Frame.开机密码地址[0],Frame.试用期密码地址[1]+1-Frame.开机密码地址[0]);;

        return cmd.toPowerOnAndProbationPeriodPwdCmdHexString(powerOnPwd,probationPeriodPwd);
    }

    public static String newStationSNAndStationNoCmd(int devAddr,String ssn,int sno){
        Cmd cmd = new Cmd(devAddr,0x10, 7000,11);;

        return cmd.toStationSNAndStationNoCmdHexString(ssn,sno);
    }

    public static String newAboutSNCmd(int devAddr,String sn,int powerOnF,int probationPeriodF,int day){
        Cmd cmd = new Cmd(devAddr,0x10, 6800,13);

        return cmd.toAboutSNCmdHexString(sn,powerOnF,probationPeriodF,day);
    }

    public static String newRecordConfig(int devAddr,int recordType,int index,int size){
        Cmd cmd = new Cmd(devAddr,0x10, 6608,3);
        return cmd.toRecordConfigCmdHexString(recordType,index,size);
    }

    public String toRecordConfigCmdHexString(int recordType,int index,int size){
        StringBuffer sb = new StringBuffer();
        appendFrameHead(sb);
        sb.append(" ");

        append2ByteIntHex(sb,start);
        sb.append(" ");

        append2ByteIntHex(sb,len);
        sb.append(" ");

        //字节
        appendIntHexSingleByte(sb,2*len);
        sb.append(" ");

        append2ByteIntHex(sb,recordType);
        sb.append(" ");

        append2ByteIntHex(sb,index);
        sb.append(" ");

        append2ByteIntHex(sb,size);
        sb.append(" ");

        appendCrcHex(sb);

        return sb.toString();
    }

    public String toAboutSNCmdHexString(String sn,int powerOnF,int probationPeriodF,int day){
        StringBuffer sb = new StringBuffer();
        appendFrameHead(sb);
        sb.append(" ");

        append2ByteIntHex(sb,start);
        sb.append(" ");

        append2ByteIntHex(sb,len);
        sb.append(" ");

        //字节
        appendIntHexSingleByte(sb,2*len);
        sb.append(" ");

        appendAscHex(sb,20,sn);
        sb.append(" ");

        append2ByteIntHex(sb,powerOnF);
        sb.append(" ");

        append2ByteIntHex(sb,probationPeriodF);
        sb.append(" ");

        append2ByteIntHex(sb,day);
        sb.append(" ");

        appendCrcHex(sb);

        return sb.toString();
    }

    public String toStationSNAndStationNoCmdHexString(String ssn,int sno){
        StringBuffer sb = new StringBuffer();
        appendFrameHead(sb);
        sb.append(" ");

        append2ByteIntHex(sb,start);
        sb.append(" ");

        append2ByteIntHex(sb,len);
        sb.append(" ");

        //字节
        appendIntHexSingleByte(sb,2*len);
        sb.append(" ");

        //站号相关-串号
        appendAscHex(sb,20,ssn);
        sb.append(" ");

        //站号相关-站号
        append2ByteIntHex(sb,sno);
        sb.append(" ");

        appendCrcHex(sb);

        return sb.toString();
    }

    public String toPowerOnAndProbationPeriodPwdCmdHexString(int powerOnPwd,int probationPeriodPwd){
        StringBuffer sb = new StringBuffer();
        appendFrameHead(sb);
        sb.append(" ");

        append2ByteIntHex(sb,start);
        sb.append(" ");

        append2ByteIntHex(sb,len);
        sb.append(" ");

        //字节
        appendIntHexSingleByte(sb,2*len);
        sb.append(" ");

        //开机密码
        appendIntHex(sb,powerOnPwd);
        sb.append(" ");

        //使用期密码
        appendIntHex(sb,probationPeriodPwd);
        sb.append(" ");

        appendCrcHex(sb);

        return sb.toString();
    }

    public String toTimeFrameCmdHexString(List<Integer> chargeValues,List<Integer> dischargeValues){
        StringBuffer sb = new StringBuffer();
        appendFrameHead(sb);
        sb.append(" ");

        append2ByteIntHex(sb,start);
        sb.append(" ");

        append2ByteIntHex(sb,len);
        sb.append(" ");

        //字节
        appendIntHexSingleByte(sb,2*len);
        sb.append(" ");

        //充电时段数
        append2ByteIntHex(sb,chargeValues.size()/4);
        sb.append(" ");

        for(int i = 0;i<24;i++){

            if(i<chargeValues.size()){
                appendIntHexSingleByte(sb,chargeValues.get(i));
                sb.append(" ");
            }else {
                appendIntHexSingleByte(sb,0);
                sb.append(" ");
            }

        }


        //放电时段数
        append2ByteIntHex(sb,dischargeValues.size()/4);
        sb.append(" ");

        for(int i = 0;i<24;i++){

            if(i<dischargeValues.size()){
                appendIntHexSingleByte(sb,dischargeValues.get(i));
                sb.append(" ");
            }else {
                appendIntHexSingleByte(sb,0);
                sb.append(" ");
            }

        }

        appendCrcHex(sb);

        return sb.toString();
    }


    /**
     * 写多个寄存器
     * @param value
     * @return
     */
    public String toHexStringWithString(String value){
        StringBuffer sb = new StringBuffer();
        appendFrameHead(sb);
        sb.append(" ");

        append2ByteIntHex(sb,start);
        sb.append(" ");

        append2ByteIntHex(sb,len);
        sb.append(" ");

        appendByteCountHexAndAscHex(sb,value);
        sb.append(" ");

        appendCrcHex(sb);

        return sb.toString();
    }

    /**
     * 写多个寄存器
     * @param value
     * @return
     */
    public String toHexStringWithMoreAddressInt(int value){
        StringBuffer sb = new StringBuffer();
        appendFrameHead(sb);
        sb.append(" ");

        append2ByteIntHex(sb,start);
        sb.append(" ");

        append2ByteIntHex(sb,len);
        sb.append(" ");

        appendByteCountHexAndIntHex(sb,value);
        sb.append(" ");

        appendCrcHex(sb);

        return sb.toString();
    }

    /**
     * 写多个寄存器
     * @param values
     * @return
     */
    public String toHexStringWithMoreAddressInts(int[] values){
        StringBuffer sb = new StringBuffer();
        appendFrameHead(sb);
        sb.append(" ");

        append2ByteIntHex(sb,start);
        sb.append(" ");

        append2ByteIntHex(sb,len);
        sb.append(" ");

        appendByteCountHexAndIntHex(sb,values);
        sb.append(" ");

        appendCrcHex(sb);

        return sb.toString();
    }

    /**
     * 写单个寄存器
     * @param value
     * @return
     */
    public String toHexStringWithInt(int value){
        StringBuffer sb = new StringBuffer();
        appendFrameHead(sb);
        sb.append(" ");

        append2ByteIntHex(sb,start);
        sb.append(" ");

        append2ByteIntHex(sb,value);
        sb.append(" ");

        appendCrcHex(sb);

        return sb.toString();
    }

    /**
     * 写单个线圈
     * @param b
     * @return
     */
    public String toHexStringWithBoolean(boolean b){
        StringBuffer sb = new StringBuffer();
        appendFrameHead(sb);
        sb.append(" ");

        append2ByteIntHex(sb,start);
        sb.append(" ");

        if(b){
            sb.append("FF 00");
        }else {
            sb.append("00 00");
        }
        sb.append(" ");

        appendCrcHex(sb);

        return sb.toString();
    }

    /**
     * 生成指令
     * @return
     */
    public String toHexString(){
        StringBuffer sb = new StringBuffer();
        appendFrameHead(sb);
        sb.append(" ");

        append2ByteIntHex(sb,start);
        sb.append(" ");

        append2ByteIntHex(sb,len);
        sb.append(" ");

        appendCrcHex(sb);

        return sb.toString();
    }

    private void appendFrameHead(StringBuffer sb){
        sb.append(intToHexString(devAddr)+" ");
        sb.append(intToHexString(fc));
    }

    private String append2ByteIntHex(StringBuffer sb ,int value){
        if(intToHexString(value).length() == 1)
            sb.append("00"+" 0"+intToHexString(value));
        else if(intToHexString(value).length() == 2)
            sb.append("00"+" "+intToHexString(value));
        else if(intToHexString(value).length() == 3)
            sb.append("0"+intToHexString(value).substring(0,1)+" "+intToHexString(value).substring(1,3));
        else if(intToHexString(value).length() == 4)
            sb.append(intToHexString(value).substring(0,2)+" "+intToHexString(value).substring(2,4));
        else{
            sb.append("FF"+" "+intToHexString(value).substring(6));
        }
        return sb.toString();
    }

    private void appendIntHex(StringBuffer sb ,int value){
        String hex = Integer.toHexString(value);
        if(hex.length()%2!=0)
            hex = "0"+hex;

        int byteCount = 4;

        String newHex = "";
        for(int i = 0;i<byteCount*2-hex.length();i++){
            newHex+="0";
        }
        hex = newHex+hex;

        StringBuffer temp = new StringBuffer();
        for(int i = 0;i<hex.length();i++){
            if(i%2==0){
                temp.append(" ");
            }
            temp.append(hex.charAt(i));
        }

        sb.append(temp.toString().trim().toUpperCase());
    }

    private String appendIntHexSingleByte(StringBuffer sb ,int value){
        if(intToHexString(value).length() == 1)
            sb.append("0"+intToHexString(value));
        else
            sb.append(intToHexString(value));
        return sb.toString();
    }

    private void appendByteCountHexAndAscHex(StringBuffer sb ,String str){
        String ascHex = ByteUtils.str2HexStr(str);
        int byteCount = len*2;
        String hexByteCount = Integer.toHexString(byteCount);
        if(hexByteCount.length()%2!=0)
            hexByteCount = "0"+hexByteCount;

        sb.append(hexByteCount.toUpperCase());
        sb.append(" ");

        String[] temp = ascHex.split(" ");
        for (int i = 0;i<byteCount;i++){
            if(i<temp.length){
                sb.append(temp[i]);
                sb.append(" ");
            }
            else {
                sb.append("00");
                if(i != byteCount-1)
                    sb.append(" ");
            }
        }
    }

    private void appendAscHex(StringBuffer sb ,int byteCount,String str){
        String ascHex = ByteUtils.str2HexStr(str);

        String[] temp = ascHex.split(" ");
        for (int i = 0;i<byteCount;i++){
            if(i<temp.length){
                sb.append(temp[i]);
                if(i != byteCount-1)
                    sb.append(" ");
            }
            else {
                sb.append("00");
                if(i != byteCount-1)
                    sb.append(" ");
            }
        }
    }

    private void appendByteCountHexAndIntHex(StringBuffer sb ,int value){
        String hex = Integer.toHexString(value);
        if(hex.length()%2!=0)
            hex = "0"+hex;
        int byteCount = len*2;

        String newHex = "";
        for(int i = 0;i<byteCount*2-hex.length();i++){
            newHex+="0";
        }
        hex = newHex+hex;

        StringBuffer temp = new StringBuffer();
        for(int i = 0;i<hex.length();i++){
            if(i%2==0){
                temp.append(" ");
            }
            temp.append(hex.charAt(i));
        }

        String hexByteCount = Integer.toHexString(byteCount);
        if(hexByteCount.length()%2!=0)
            hexByteCount = "0"+hexByteCount;

        sb.append(hexByteCount.toUpperCase());
        sb.append(temp.toString().toUpperCase());
    }

    private void appendByteCountHexAndIntHex(StringBuffer sb ,int[] values){
        StringBuffer temp = new StringBuffer();
        int byteCount = len*2;
        String hexByteCount = Integer.toHexString(byteCount);
        if(hexByteCount.length()%2!=0)
            hexByteCount = "0"+hexByteCount;
        sb.append(hexByteCount.toUpperCase());

        for (int value : values){
            String hex = Integer.toHexString(value);
            if(hex.length()%2!=0)
                hex = "0"+hex;

            String newHex = "";
            for(int i = 0;i<2*2-hex.length();i++){
                newHex+="0";
            }
            hex = newHex+hex;

            for(int i = 0;i<hex.length();i++){
                if(i%2==0){
                    temp.append(" ");
                }
                temp.append(hex.charAt(i));
            }

        }
        sb.append(temp.toString().toUpperCase());
    }

    private void appendCrcHex(StringBuffer sb){
        byte[] bytes = ByteUtils.hexStringToBytes(sb.toString().trim());
        sb.append(ByteUtils.bytesToHexStringSplitBySpace(Crc16Utils.crc16(bytes)));
    }

    private static String intToHexString(int data) {
        StringBuilder result = new StringBuilder("");
        String hv = Integer.toHexString(data);
        if (hv.length() < 2) {
            result.append(0);
        }
        result.append(hv);

        return result.toString().toUpperCase();
    }

}

package com.kehua.energy.monitor.app.model.entity;

import com.kehua.energy.monitor.app.utils.ByteUtils;
import com.kehua.energy.monitor.app.utils.Crc16Utils;
import com.orhanobut.logger.Logger;

public class ModbusResponse {

    private int addr;
    private int fc;
    private int len;
    private byte[] bytesDat;
    private String data;

    public boolean isSuccess(){
        if(Crc16Utils.checkCrc16(data)){
            //crc验证数据准确性

            byte[] bytes = ByteUtils.hexStringToBytes(data);

            if(bytes.length<3){
                Logger.e("data 解析失败");
                return false;
            }

            addr = bytes[0] & 0xFF;

            fc = bytes[1] & 0xFF;

            if(fc == 0x10||fc == 0x06||fc == 0x05){
                //写 指令回复
                return true;
            }


            if(fc >= 0x80){
                int error = bytes[2] & 0xFF;

                switch (error){
                    case 0x01:
                        Logger.e("非法的功能码");
                        break;
                    case 0x02:
                        Logger.e("非法的数据地址");
                        break;
                    case 0x03:
                        Logger.e("非法的数据值");
                        break;
                    case 0x04:
                        Logger.e("服务故障");
                        break;
                    case 0x10:
                        Logger.e("错误的寄存器设定值");
                        break;
                    case 0x11:
                        Logger.e("无权限");
                        break;
                }

                return false;
            }else{
                len = bytes[2] & 0xFF;

                bytesDat = new byte[len];

                System.arraycopy(bytes,3,bytesDat,0,len);
            }

            return true;
        }else
            Logger.e("crc 验证失败");

        return false;
    }

    public String getData() {
        return data;
    }

    public int getAddr() {
        return addr;
    }

    public int getFc() {
        return fc;
    }

    public int getLen() {
        return len;
    }

    public byte[] getBytesDat() {
        return bytesDat;
    }
}


package com.kehua.energy.monitor.app.utils;

public class PasswordUtils {

    public static long createPassword(int seed,String deviceNo)
    {
        int length = deviceNo.length();
        long hash = 0;
        while (length-- != 0){
            hash = hash*seed+deviceNo.charAt(length);
            hash = getUnSignedLong(hash);
        }

        hash = hash%1000000;
        hash = getUnSignedLong(hash);

        if(hash < 10){
            hash = hash + 333330;
        }else if(hash < 100){
            hash = hash + 333300;
        }else if(hash < 1000){
            hash = hash + 333000;
        }else if(hash < 10000){
            hash = hash + 330000;
        }else if(hash < 100000){
            hash = hash + 300000;
        }

        return getUnSignedLong(hash);
    }

    private static long getUnSignedLong(long l) {
        return getLong(longToDWORD(l), 0);
    }

    // 将long型数据转换为Dword的字节数组（C/C++的无符号整数）
    private static byte[] longToDWORD(long value) {

        byte[] data = new byte[4];

        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (value >> (8 * i));
        }

        return data;
    }

    // 将C/C++的无符号 DWORD类型转换为java的long型
    private static long getLong(byte buf[], int index) {

        int firstByte = (0x000000FF & ((int) buf[index]));
        int secondByte = (0x000000FF & ((int) buf[index + 1]));
        int thirdByte = (0x000000FF & ((int) buf[index + 2]));
        int fourthByte = (0x000000FF & ((int) buf[index + 3]));

        long unsignedLong = ((long) (firstByte | secondByte << 8 | thirdByte << 16 | fourthByte << 24)) & 0xFFFFFFFFL;

        return unsignedLong;
    }


}
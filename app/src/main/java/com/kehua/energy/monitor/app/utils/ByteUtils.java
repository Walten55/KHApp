package com.kehua.energy.monitor.app.utils;

/**
 * 字节工具类
 * 
 * @author wangjinshui
 */
public class ByteUtils {
	/**
	 * @param data
	 *            字节
	 * @return 用16进制显示指定的字节
	 */
	public static String byteToHexString(byte data) {
		StringBuilder result = new StringBuilder("");
		int v = data & 0xFF;
		String hv = Integer.toHexString(v);
		if (hv.length() < 2) {
			result.append(0);
		}
		result.append(hv);

		return result.toString().toUpperCase();
	}
	

	/**
	 * @param datas
	 *            字节数组
	 * @return 用16进制显示指定的字节数组，如 new byte[] { 0x01, 0xFF }，结果为 01FF
	 */
	public static String bytesToHexString(byte[] datas) {
		if (datas == null || datas.length <= 0)
			return null;

		StringBuilder result = new StringBuilder("");
		for (byte data : datas) {
			result.append(byteToHexString(data));
		}

		return result.toString();
	}

	
	public static String bytesToHexStringSplitBySpace(byte[] datas) {
		if (datas == null || datas.length <= 0)
			return null;

		StringBuilder result = null;
		for (byte data : datas) {
			if (result == null) {
				result = new StringBuilder("");
			} else {
				result.append(" ");
			}

			result.append(byteToHexString(data));
		}

		return result.toString();
	}

	
	/**
	 * @param hexString
	 *            16进制字符串
	 * @return 字节数组
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		// 去除空格并转为大写
		hexString = hexString.replaceAll(" ", "").toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	
	
	/**
	 * 将src的内容复制到dest指定位置索引开始
	 * @param src 源字节数组
	 * @param dest 目标字节数组
	 * @param destIndex 目标数组开始索引，从0开始
	 */
	public static byte[] copyByteArray(byte[] src, byte[] dest, int destIndex) {
		if (src == null || destIndex < 0)
			return dest;
		
		byte[] result = null;
		
		if (dest == null) {
			result = new byte[destIndex + src.length];
		} else if (destIndex + dest.length < src.length) {
			result = new byte[destIndex + src.length];
			System.arraycopy(dest, 0, result, 0, dest.length);
		} else {
			result = dest;
		}
		
		for (int i = 0, len = src.length; i < len; i++) {
			result[destIndex + i] = src[i];
		}
		
		return result;
	}
	
	
	/**
	 * 将多个字节数组按顺序合并成一个字节数组
	 * @param array 字节数组
	 * @return 包含所有参数内容的一个字节数组
	 */
	public static byte[] union(byte[]... array) {
		if (array == null)
			return new byte[0];
		
		int length = 0;
		for (byte[] datas : array) {
			length += (datas == null ? 0 : datas.length);
		}
		
		byte[] result = new byte[length];
		int index = 0;
		for (byte[] datas : array) {
			if (datas == null)
				continue;
			
			System.arraycopy(datas, 0, result, index, datas.length);
			index += datas.length;
		}
		
		return result;
	}
	
	
	/**
	 * @param data 字节
	 * @param count 次数
	 * @return 长度为指定数量且每个字节都同为指定数据的字节数组。
	 */
	public static byte[] clone(byte data, int count) {
		if (count <= 0)
			return new byte[0];
		
		byte[] result = new byte[count];
		for (int i = 0; i < count; i++) {
			result[i] = data;
		}
		return result;
	}
	
	
	/**
	 * @param bytes 字节数组
	 * @param beginIndex 开始索引，从0开始
	 * @return 指定索引开始的字节数组子集
	 */
	public static byte[] subArray(byte[] bytes, int beginIndex) {
		if (bytes == null || beginIndex > bytes.length)
			return new byte[0];
		
		byte[] result = new byte[bytes.length - beginIndex];
		for (int i = 0, j = beginIndex, len = bytes.length; j < len; i++, j++) {
			result[i] = bytes[j];
		}
		return result;
	}

	public static double bytes2Double(byte[] arr) {
		byte[] temp = new byte[8];
		if(arr.length != 8){
			copyByteArray(arr,temp, arr.length);
		}else {
			copyByteArray(arr,temp,0);
		}

		long value = 0;
		for (int i = 0; i < 8; i++) {
			value |= ((long) (temp[i] & 0xff)) << (8 * i);
		}
		return Double.longBitsToDouble(value);
	}

	public static byte[] double2Bytes(double d) {
		long value = Double.doubleToRawLongBits(d);
		byte[] byteRet = new byte[8];
		for (int i = 0; i < 8; i++) {
			byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
		}
		return byteRet;
	}

	public static String bytes2String(byte[] arr){
		try {
			int length =  arr.length;
			for (int i = 0; i < arr.length; ++i) {
				if (arr[i] == 0) {
					length = i;
					break;
				}
			}
			return new String(arr, 0, length, "UTF-8");
		} catch (Exception e) {
			return "";
		}
	}

	public static int bytes2Int(byte[] arr) {
		byte[] temp = new byte[4];
		if(arr.length != 4){
			copyByteArray(arr,temp,arr.length);
		}else {
			copyByteArray(arr,temp,0);
		}

		int intValue = 0;
		for (int i = 0; i < temp.length; i++) {
			intValue += (temp[i] & 0xFF) << (8 * (3 - i));
		}
		return intValue;
	}

	public static int bytes2SignedInt (byte[] arr)      // signed
	{
		if(arr.length == 2)
			return (arr[0] << 8) | (arr[1] & 0xFF);
		else if(arr.length == 4)
			return (arr[0] << 24) | (arr[1] & 0xFF) << 16 | (arr[2] & 0xFF) << 8 | (arr[3] & 0xFF);
		else
			return 0;
	}


	public static String str2HexStr(String str) {

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}


	private ByteUtils() {
	}
}

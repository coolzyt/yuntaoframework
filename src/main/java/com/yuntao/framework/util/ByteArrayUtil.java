package org.yuntao.framework.util;
/**
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-3-17
 * @author zhaoyuntao
 * 
 */
public class ByteArrayUtil {
    public static final int BYTE_MASK = 0xff;

    /**
     * 把一个字节数组转为16进制字符串表示,一个字节表示为两个字符
     * 主要用于编码或者加密表示
     * eg: byte[] bs={15,17}
     *     byteArray2HexString(bs)的结果是 0ff1
     * @param data 字符数组
     * @return 16进制字符串
     */
    public static String byteArray2HexString(byte[] data) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String temp = Integer.toHexString(data[i] & BYTE_MASK);
            if (temp.length() == 1)
                temp = "0" + temp;
            sb.append(temp);
        }
        return sb.toString().toUpperCase();
    }

    /**
     * Base1编码
     * @param data
     * @return base16编码
     */
    public static String base16(byte[] data) {
        return byteArray2HexString(data);
    }

    /**
     * @param base16
     * @return 将BASE16的字符串还原为字节数组
     */
    public static byte[] base16decode(String base16Str){
		byte[] ret = new byte[base16Str.length()/2];
		int j = 0;
		for(int i=0;i<base16Str.length();i+=2){
			ret[j++] = (byte)(Integer.parseInt(""+base16Str.charAt(i)+base16Str.charAt(i+1),16));
		}
		return ret;
	}
    /**
     * 字节数组转成一个int整数
     * 字节数组长度必须是4
     * 高字节在前的规则,也就是搞字节在低地址
     * @param bs  长度为4的字节数组
     * @return  整数
     */
    public static int byteArray2Int(byte[] bs) {
        if (bs.length != 4)
            throw new IllegalArgumentException();
        int res = 0;
        res |= (bs[0] & BYTE_MASK) << 24;
        res |= (bs[1] & BYTE_MASK) << 16;
        res |= (bs[2] & BYTE_MASK) << 8;
        res |= (bs[3] & BYTE_MASK);
        return res;
    }

    /**
     * BigEndian：低地址存放最高有效位
     * @param bs 
     * @return 整数
     */
    public static int byteArrayBigEndian2Int(byte[] bs) {
        return byteArray2Int(bs);
    }

    /**
     * 低地址存放最低有效位
     * @param bs
     * @return 整数
     */
    public static int byteArrayLittleEndian2Int(byte[] bs) {
        if (bs.length != 4)
            throw new IllegalArgumentException();
        int res = 0;
        res |= (bs[3] & BYTE_MASK) << 24;
        res |= (bs[2] & BYTE_MASK) << 16;
        res |= (bs[1] & BYTE_MASK) << 8;
        res |= (bs[0] & BYTE_MASK);
        return res;

    }

    /**
     * 整数转成字节数组
     * @param num
     * @return 字节数组
     * @see #byteArray2Int(byte[])
     */
    public static byte[] int2ByteArray(int num) {
        byte[] b = new byte[4];
        b[0] = (byte) (num >>> 24);
        b[1] = (byte) (num >>> 16);
        b[2] = (byte) (num >>> 8);
        b[3] = (byte) num;
        return b;
    }

    /**
     * 整数转成字节数组
     * @param num
     * @return 字节数组
     * @see #byteArrayBigEndian2Int(byte[])
     */
    public static byte[] int2ByteArrayBigEndian(int num) {
        return int2ByteArray(num);
    }

    /**
     * 整数转成字节数组
     * @param num
     * @return 字节数组
     * @see #byteArrayLittleEndian2Int(byte[])
     */
    public static byte[] int2ByteArrayLittleEndian(int num) {
        byte[] b = new byte[4];
        b[3] = (byte) (num >>> 24);
        b[2] = (byte) (num >>> 16);
        b[1] = (byte) (num >>> 8);
        b[0] = (byte) num;
        return b;
    }
}

package com.zjht.unified.common.core.util;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * 未明确字节序时，默认是BigEndian
 */
public class ByteUtils {

    /**
     * MD5加密，32位大写
     */
    public static String getMD5(String message) {
        String md5str;
        try {
            //1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");

            //2 将消息变成byte数组
            byte[] input = message.getBytes();

            //3 计算后获得字节数组,这就是那128位了
            byte[] buff = md.digest(input);

            //4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            md5str = bytesToHex(buff);

        } catch (Exception e) {
            e.printStackTrace();
            md5str = null;
        }
        return md5str;
    }

    /**
     * 二进制转十六进制
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder md5str = new StringBuilder();
        //把数组每一字节换成16进制连成md5字符串
        int digital;
        for (byte aByte : bytes) {
            digital = aByte;

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }

    /**
     * 2个int转换为float类型（32位），如modbus协议中，Real类型占用2个寄存器，每个寄存器读取出一个int
     */
    public static float twoInts2Float32(int n1, int n2) {
        byte[] bytes = new byte[4];
        System.arraycopy(int2bytes(n1), 0, bytes, 0, 2);
        System.arraycopy(int2bytes(n2), 0, bytes, 2, 2);
        int n = bytes2UnsignedInt(bytes);
        return Float.intBitsToFloat(n);
    }

    public static double fourInts2Double(int n1, int n2, int n3, int n4) {
        byte[] bytes = new byte[8];
        System.arraycopy(int2bytes(n1), 0, bytes, 0, 2);
        System.arraycopy(int2bytes(n2), 0, bytes, 2, 2);
        System.arraycopy(int2bytes(n3), 0, bytes, 4, 2);
        System.arraycopy(int2bytes(n4), 0, bytes, 6, 2);
        return registersToDouble(bytes);
    }

    public static final double registersToDouble(byte[] bytes) {
        return Double.longBitsToDouble(((((long) (bytes[0] & 0xff) << 56) | ((long) (bytes[1] & 0xff) << 48)
                | ((long) (bytes[2] & 0xff) << 40) | ((long) (bytes[3] & 0xff) << 32) | ((long) (bytes[4] & 0xff) << 24)
                | ((long) (bytes[5] & 0xff) << 16) | ((long) (bytes[6] & 0xff) << 8) | (bytes[7] & 0xff))));
    }

    /**
     * 将一个4字节的float转换为2个2字节的int型整数
     */
    public static short[] float2TwoInts(float f) {
        return int2TwoInts(Float.floatToIntBits(f));
    }

    /**
     * 将一个4字节的int型整数转换为2个2字节的int型整数
     */
    public static short[] int2TwoInts(int n) {
        short[] result = new short[2];
        result[0] = (short) (n >> 16 & 0xFFFF);
        result[1] = (short) (n & 0x0000FFFF);
        return result;
    }

    public static byte[] int2bytes(int n) {
        return int2bytes(n, 2);
    }

    /**
     * 保留length个字节
     */
    public static byte[] int2bytes(int n, int length) {
        if (length > 4 || length < 1) {
            length = 4;
        }
        byte[] b = new byte[length];
        for (int i = length; i > 0; i--) {
            b[i - 1] = (byte) (n >> ((length - i) * 8));
        }
        return b;
    }

    public static int bytes2UnsignedInt(byte[] bytes) {
        if (bytes == null || bytes.length < 1)
            return 0;
        byte[] b = new byte[4]; // int占用4个字节
        if (bytes.length >= b.length) {   // 取前4个字节
            System.arraycopy(bytes, 0, b, 0, b.length);
        } else {
            int zeroLen = b.length - bytes.length;
            System.arraycopy(bytes, 0, b, zeroLen, bytes.length);
            for (int i = 0; i < zeroLen; i++) {
                b[i] = 0;
            }
        }
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static void main(String[] args) {
        System.out.println(twoInts2Float32(16968, 0));
        System.out.println(Float.intBitsToFloat(1112014848));

        System.out.println(twoInts2Float32(16804, 49152));
        System.out.println(Float.intBitsToFloat(1101316096));


        System.out.println(Float.intBitsToFloat(0));

        float f = 345.678f;
        int n = Float.floatToIntBits(f);
        System.out.println(Integer.toHexString(n));
        short[] a = int2TwoInts(n);
        System.out.println(Arrays.toString(a));
        System.out.println(twoInts2Float32(a[0], a[1]));


        System.out.println(getMD5("zjht&2021-10-27"));
    }

}

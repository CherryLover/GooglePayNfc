package com.anviz.googlepaynfc;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Random;

/**
 * Created by joe.zhou on 10/28/2017.
 */

public final class util_byte {

    private static String[] binaryArray =
            {"0000", "0001", "0010", "0011",
                    "0100", "0101", "0110", "0111",
                    "1000", "1001", "1010", "1011",
                    "1100", "1101", "1110", "1111"};

    // byte[]转低16位int型数据
    public static int byteToInt2(byte[] b) {
        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 1; i >= 0; i--) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }

    public static int byteToInt2(byte[] b, int start) {
        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = start + 1; i >= start; i--) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }

    // byte[] 数据按16进制String输出
    public static String byte2hex(byte[] buffer) {
        String h = "";

        if (buffer == null) {
            return h;
        }
        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (1 == temp.length()) {
                temp = "0" + temp;
            }

            h = h + " " + temp;
        }

        return h;
    }

    public static String byte2hex(byte[] buffer, int len) {
        String h = "";

        for (int i = 0; i < len; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (1 == temp.length()) {
                temp = "0" + temp;
            }

            h = h + " " + temp;
        }

        return h;
    }

    // String型数据转byte[]
    public static byte[] toByteArray(String number) {
        byte[] change = new byte[4];
        long data = Long.parseLong(number);
        int ArrayLen = 4;
        byte[] pack_length = toByteArray(data, ArrayLen);
        change = pack_length;
        return change;
    }

    // int型数据转byte[]
    public static byte[] toByteArray(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
        for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
        }

        return bLocalArr;
    }

    // String型数据转byte[]
    public static byte[] toByteArraylong(String number) {
        byte[] change = new byte[4];
        long data = Long.parseLong(number);
        int ArrayLen = 4;
        byte[] pack_length = toByteArray(data, ArrayLen);
        change = pack_length;
        return change;
    }

    // int型数据转byte[]
    public static byte[] toByteArray(long iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
        for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
        }

        return bLocalArr;
    }

    /**
     * 本方法适用于(高位在前，低位在后)的顺序。
     *
     * @return byte数组
     */
//	public static byte[] intToBytes2(int value) {
//		byte[] bLocalArr = new byte[2];
//		bLocalArr[0] = (byte) ((value & 0x0000FF00) >> 8);
//		bLocalArr[1] = (byte) ((value & 0x000000FF));
//
//		return bLocalArr;
//	}


    // String型数据转byte[]1位数
    public static byte[] toByteArray1(String number) {
        byte[] change = new byte[4];
        int data = Integer.parseInt(number);
        int ArrayLen = 1;
        byte[] pack_length = toByteArray(data, ArrayLen);
        change = pack_length;
        return change;
    }

    public static long longFrom8Bytes(byte[] input, int offset, boolean littleEndian) {
        long value = 0;
        // 循环读取每个字节通过移位运算完成long的8个字节拼装
        for (int count = 0; count < 8; ++count) {
            int shift = (littleEndian ? count : (7 - count)) << 3;
            value |= ((long) 0xff << shift) & ((long) input[offset + count] << shift);
        }
        return value;
    }

    // byte[]转32位int型数据
    public static int byteToInt4(byte[] b, int start) {
        if (b == null) {
            return 0;
        }

        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 3; i >= 0; i--) {
            n <<= 8;
            temp = b[start + i] & mask;
            n |= temp;
        }
        return n;
    }

    public static int byteToInt4(byte[] b) {
        return byteToInt4(b, 0);
    }


    public static int getBit(byte data, int position) {
        return (data >> position) & 1;
    }

    //b为传入的字节，start是起始位，length是长度，如要获取bit0-bit4的值，则start为0，length为5
    public static int getBits(byte b, int start, int length) {
        int bit = (int) ((b >> start) & (0xFF >> (8 - length)));
        return bit;
    }

    // byte[]转低8位int型数据
    public static int byteToInt1(byte[] b) {
        if (b.length == 0) {
            return 0;
        }
        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 0; i >= 0; i--) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }

    public static int byteToInt1(byte b) {
        int mask = 0xff;
        int temp = 0;
        int n = 0;
        n <<= 8;
        temp = b & mask;
        n |= temp;
        return n;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式
     * 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF, 0xD9}
     *
     * @param src String
     * @return byte[]
     */
    public static byte[] HexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        int j = 0;
        for (int i = 0; i < ret.length; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    // 生成一个6到8位的随机整数用作UL1添加新用户的id或password
    public static int getRandom() {
        Random rand = new Random();
        int randNum = rand.nextInt(99900000) + 100000;

        return randNum;
    }

    // 生成一个8位的随机整数
    public static int getRandomLen8() {
        Random rand = new Random();
        int randNum = rand.nextInt(90000000) + 10000000;

        return randNum;
    }

    // 从云上读取的卡号转成16进制的String
    public static String to16String(String userCard) {
        long aa = Long.parseLong(userCard);
        String bb = Long.toHexString(aa);

        return bb;
    }

    // 从锁中读取的卡号按16进制转成String
    public static String byteTo16String(byte[] Param, int start) {
        byte[] card = new byte[4];
        System.arraycopy(Param, start, card, 0, card.length);
        String cardid = util_byte.byte2hex(card).replace(" ", "");
        long card_id = Long.parseLong(cardid, 16);

        return card_id + "";
    }

    // byte[] 数据按16进制String输出
    public static String byte2hexnospace(byte[] buffer) {
        String h = "";
        if (buffer == null) {
            return h;
        }
        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (1 == temp.length()) {
                temp = "0" + temp;
            }

            h = h + temp;
        }

        return h;
    }

    /**
     * 4字节数据转换为long型，高低字节需转换  总共4字节
     *
     * @param buffer
     * @return
     */
    public static long byte2hexForNum(byte[] buffer) {
        return byte2hexForNum(buffer, 4);
    }

    public static long byte2hexForNum(byte[] buffer, int len) {
        if (buffer == null) {
            return 0;
        }
        byte[] numBytes = new byte[len];
        for (int i = len - 1; i >= 0; i--) {
            numBytes[len - 1 - i] = buffer[i];
        }
        String numStr = byte2hexnospace(numBytes);
        long num = Long.parseLong(numStr, 16);
        return num;
    }

    public static String bytesToAscii(byte[] bytes, int offset, int dateLen) {
        if ((bytes == null) || (bytes.length == 0) || (offset < 0) || (dateLen <= 0)) {
            return null;
        }
        if ((offset >= bytes.length) || (bytes.length - offset < dateLen)) {
            return null;
        }

        int len = dateLen;
        String asciiStr = null;
        for (int i = 0; i < dateLen; i++) {
            if (bytes[i] == 0) {
                len = i;
                break;
            }
        }
        byte[] data = new byte[len];
        System.arraycopy(bytes, offset, data, 0, len);
        try {
            asciiStr = new String(data, "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return asciiStr;
    }

    public static String bytesToAscii(byte[] bytes, int dateLen) {
        return bytesToAscii(bytes, 0, dateLen);
    }

    public static String bytesToAscii(byte[] bytes) {
        return bytesToAscii(bytes, 0, bytes.length);
    }


    public static String byte2mac(byte[] buffer) {
        String h = "";

        if (buffer == null) {
            return h;
        }
        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (1 == temp.length()) {
                temp = "0" + temp;
            }
            if (i == 0) {
                h = temp;
            } else {
                h = h + ":" + temp;
            }
        }

        return h.toUpperCase();
    }

    public static String passwordAddLength(String mPassword) {
        if (TextUtils.isEmpty(mPassword)) {
            return "";
        }
        byte[] uPassword = toByteArray(mPassword);
        uPassword[3] |= mPassword.length() << 4;
        String newPassword = "";
        for (int i = uPassword.length - 1; i >= 0; i--) {
            String temp = Integer.toHexString(uPassword[i] & 0xFF);
            if (1 == temp.length()) {
                temp = "0" + temp;
            }
            newPassword = newPassword + temp;
        }
        long num = Long.parseLong(newPassword, 16);
        return String.valueOf(num);
    }

    public static String passwordRemoveLength(String mPassword) {
        String mRetuenPwd = "";
        try {
            byte[] bPwd = toByteArray(mPassword);
            String newPassword = "";

            for (int i = bPwd.length - 1; i >= 0; i--) {
                String temp = Integer.toHexString(bPwd[i] & 0xFF);
                if (1 == temp.length()) {
                    temp = "0" + temp;
                }
                newPassword = newPassword + temp;
            }
            String mLengthStr = newPassword.substring(0, 1);
            int mLength = Integer.parseInt(mLengthStr);
            if (mLength == 0) {
                mRetuenPwd = mPassword;
            } else {
                String noLengthStr = newPassword.substring(1);
                long mFPwd = Long.parseLong(noLengthStr, 16);
                String mFPwdStr = mFPwd + "";
                mRetuenPwd = mFPwdStr;
                if (mLength != mFPwdStr.length()) {
                    for (int i = 0; i < mLength - mFPwdStr.length(); i++) {
                        mRetuenPwd = "0" + mRetuenPwd;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mRetuenPwd = mPassword;
        }
        return mRetuenPwd;
    }

    /**
     * @param bArray
     * @return 二进制数组转换为二进制字符串
     */
    public static String bytes2BinStr(byte[] bArray) {

        String outStr = "";
        int pos = 0;
        for (byte b : bArray) {
            //高四位
            pos = (b & 0xF0) >> 4;
            outStr += binaryArray[pos];
            //低四位
            pos = b & 0x0F;
            outStr += binaryArray[pos];
        }
        return outStr;
    }

    /**
     * @param hexStr
     * @return
     * @description 将16进制转换为二进制
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static boolean isNumericZidai(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param binarySource
     * @return int
     * @Description: 二进制转换成十进制
     */
    public static int binaryToDecimal(String binarySource) {
        BigInteger bi = new BigInteger(binarySource, 2);
        return Integer.parseInt(bi.toString());
    }

    public static int signedHex2Num(byte[] data) {
        byte[] numBytes = new byte[data.length];
        for (int i = 0; i < numBytes.length; i++) {
            numBytes[i] = data[data.length - 1 - i];
        }
        String dataStr = byte2hexnospace(numBytes);
        BigInteger bi = new BigInteger(dataStr, 16);
        return bi.intValue();
    }
}


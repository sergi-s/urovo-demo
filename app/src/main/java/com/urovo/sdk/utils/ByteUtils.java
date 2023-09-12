//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.urovo.sdk.utils;

import java.util.Locale;

public class ByteUtils {
    public ByteUtils() {
    }

    public static String bin2hex(String input) {
        StringBuilder sb = new StringBuilder();
        int len = input.length();
        System.out.println("原数据长度：" + (len / 8) + "字节");

        for (int i = 0; i < len / 4; i++){
            //每4个二进制位转换为1个十六进制位
            String temp = input.substring(i * 4, (i + 1) * 4);
            int tempInt = Integer.parseInt(temp, 2);
            String tempHex = Integer.toHexString(tempInt).toUpperCase();
            sb.append(tempHex);
        }

        return sb.toString();
    }

    public static final String bytes2HexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);

        for (int i = 0; i < bArray.length; ++i) {
            String sTemp = Integer.toHexString(255 & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }

            sb.append(sTemp.toUpperCase(Locale.getDefault()));
        }

        return sb.toString();
    }

    private static byte bcd2asc(byte ucBcd) {
        ucBcd = (byte) (ucBcd & 15);
        byte ucAsc;
        if (ucBcd <= 9) {
            ucAsc = (byte) (ucBcd + 48);
        } else {
            ucAsc = (byte) (ucBcd + 65 - 10);
        }

        return ucAsc;
    }

    public static void bcd2Asc(byte[] sAscBuf, byte[] sBcdBuf, int iAscLen) {
        int j = 0;

        int i;
        for (i = 0; i < iAscLen / 2; ++i) {
            sAscBuf[j] = (byte) ((sBcdBuf[i] & 240) >> 4);
            sAscBuf[j] = bcd2asc(sAscBuf[j]);
            ++j;
            sAscBuf[j] = (byte) (sBcdBuf[i] & 15);
            sAscBuf[j] = bcd2asc(sAscBuf[j]);
            ++j;
        }

        if (iAscLen % 2 != 0) {
            sAscBuf[j] = (byte) ((sBcdBuf[i] & 240) >> 4);
            sAscBuf[j] = bcd2asc(sAscBuf[j]);
        }

    }

    public static void asc2Bcd(byte[] sBcdBuf, byte[] sAscBuf, int iAscLen) {
        int j = 0;

        for (int i = 0; i < (iAscLen + 1) / 2; ++i) {
            sBcdBuf[i] = (byte) (asc2bcd(sAscBuf[j++]) << 4);
            if (j >= iAscLen) {
                sBcdBuf[i] = (byte) (sBcdBuf[i] | 0);
            } else {
                sBcdBuf[i] |= asc2bcd(sAscBuf[j++]);
            }
        }

    }

    public static byte asc2bcd(byte ucAsc) {
        byte ucBcd;
        if (ucAsc >= 48 && ucAsc <= 57) {
            ucBcd = (byte) (ucAsc - 48);
        } else if (ucAsc >= 65 && ucAsc <= 70) {
            ucBcd = (byte) (ucAsc - 65 + 10);
        } else if (ucAsc >= 97 && ucAsc <= 102) {
            ucBcd = (byte) (ucAsc - 97 + 10);
        } else if (ucAsc > 57 && ucAsc <= 63) {
            ucBcd = (byte) (ucAsc - 48);
        } else {
            ucBcd = 15;
        }

        return ucBcd;
    }

    public static String bcd2Str(byte[] b) {
        char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder sb = new StringBuilder(b.length * 2);

        for (int i = 0; i < b.length; ++i) {
            sb.append(HEX_DIGITS[(b[i] & 240) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 15]);
        }

        return sb.toString();
    }

    public static byte[] getxor(byte[] a, byte[] b, int len) {
        for (int i = 0; i < len; ++i) {
            a[i] ^= b[i];
        }

        return a;
    }

    public static byte[] str2HexByte(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];

            for (int i = 0; i < len; ++i) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }

            return buffer;
        }
    }

    public static void btaByIndex(byte[] sBcdBuf, int sBcdIndex, byte[] sAscBuf, int sAscIndex, int iAscLen) {
        byte[] Asctemp = new byte[iAscLen];
        byte[] Bcdtemp = new byte[(iAscLen + 1) / 2];
        System.arraycopy(sBcdBuf, sBcdIndex, Bcdtemp, 0, (iAscLen + 1) / 2);
        bcd2Asc(Asctemp, Bcdtemp, iAscLen);
        System.arraycopy(Asctemp, 0, sAscBuf, sAscIndex, iAscLen);
    }

    /**
     * 16�����ַ�ת������
     *
     * @param hex
     * @return ת�����
     */
    public static byte hex2byte(char hex) {
        if (hex <= 'f' && hex >= 'a') {
            return (byte) (hex - 'a' + 10);
        }

        if (hex <= 'F' && hex >= 'A') {
            return (byte) (hex - 'A' + 10);
        }

        if (hex <= '9' && hex >= '0') {
            return (byte) (hex - '0');
        }

        return 0;
    }


    /**
     * 16���Ʊ�ʾ���ַ���ת����������
     *
     * @param data
     * @return ת�����
     */
    public static byte[] hexString2Bytes(String data) {
        if (data == null)
            return null;
        byte[] result = new byte[(data.length() + 1) / 2];
        if ((data.length() & 1) == 1) {
            data += "0";
        }
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (hex2byte(data.charAt(i * 2 + 1)) | (hex2byte(data.charAt(i * 2)) << 4));
        }
        return result;
    }

    public static int comparabytes(byte[] temp1, byte[] temp2, int len) {
        for (int i = 0; i < len; ++i) {
            if (temp1[i] != temp2[i]) {
                return -1;
            }
        }

        return 0;
    }

}

package com.urovo.sdk.aid;

import android.text.TextUtils;

import com.urovo.sdk.utils.DateUtil;

import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author Administrator
 */
public class StringUtil {

	// 转成以FMT格式字符
	public static String FormatvalueByLeft(String str, String value) {
		try {
			int len = value.length();
			if (str.length() >= len) {
				return str;
			} else {
				String temp = "";

				temp = value.substring(0, value.length() - str.length());
				temp += str;
				return temp;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return str;
	}

	public static String addSpaceRight(String val, int length) {
		if(TextUtils.isEmpty(val)){
			val = "";
		}
		int addLen = length-val.length();
		for (int i = 0; i < addLen; i++)
			val += " ";
		return val;
	}

	/**
	 * 手机号验证
	 *
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isMobile(String str) {
		String from = "^[1][3-9][0-9]{9}$";
		return is(from, str);
	}

	/**
	 * 获取随机字符串
	 *
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "ABCDEF0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * 获取随机字符串
	 *
	 * @param length
	 * @return
	 */
	public static String getRandomString2(int length) { // length表示生成字符串的长度
		String base = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * 奇校验
	 * <ul><li>就是让原有数据序列中（包括你要加上的一位）1的个数为奇数</li>
	 * <li>1000110（0）你必须添0这样原来有3个1已经是奇数了所以你添上0之后1的个数还是奇数个</li>
	 * </ul>
	 * @param bytes 长度为8的整数倍
	 * @param parity 0:奇校验,1:偶校验
	 * @return
	 * @throws Exception
	 */
	public static byte[] parityOfOdd(byte[] bytes, int parity) throws Exception{
		if(bytes == null || bytes.length % 8 != 0){
			throw new Exception("数据错误!");
		}
		if(!(parity == 0 || parity == 1)){
			throw new Exception("参数错误!");
		}
		byte[] _bytes = bytes;
		String s; // 字节码转二进制字符串
		char[] cs ; // 二进制字符串转字符数组
		int count; // 为1的总个数
		boolean lastIsOne; // 最后一位是否为1
		for(int i=0;i<_bytes.length;i++){
			// 初始化参数
			s = Integer.toBinaryString((int)_bytes[i]); // 字节码转二进制字符串
			cs = s.toCharArray();// 二进制字符串转字符数组
			count = 0;// 为1的总个数
			lastIsOne = false;// 最后一位是否为1
			for(int j=0;j<s.length();j++){
				if(cs[j] == '1'){
					count++;
				}
				if(j == (cs.length -1)){ // 判断最后一位是否为1
					if(cs[j] == '1'){
						lastIsOne = true;
					} else {
						lastIsOne = false;
					}
				}
			}
			// 偶数个1时
			if(count % 2 == parity){
				// 最后一位为1,变为0
				if(lastIsOne){
					_bytes[i] = (byte) (_bytes[i] - 0x01);
				} else {
					// 最后一位为0,变为1
					_bytes[i] = (byte) (_bytes[i] + 0x01);
				}
			}
		}
		return _bytes;
	}

	/**
	 * 验证邮箱
	 *
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		return is(str, email);
	}

	public static boolean isIpv4(String ipAddress) {
		String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
				+ "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
				+ "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
				+ "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
		return is(ip, ipAddress);
	}

	/**
	 * @param from
	 *            正则规则
	 * @param check
	 *            需要验证的字符串
	 * @return
	 */
	private static boolean is(String from, String check) {
		return Pattern.compile(from).matcher(check).matches();
	}

	/**
	 * 添加空格
	 *
	 * @param string
	 *            值长度
	 * @param length
	 *            总长度
	 * @param isleft
	 *            True为左边加空格,False为后补空格
	 * @return
	 */
	public static String addsp(String string, int length, boolean isleft) {
		if(TextUtils.isEmpty(string)){
			string = "";
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length - string.length(); i++) {
			buffer.append(" ");
		}
		if (isleft)
			return buffer.append(string).toString();
		else
			return string + buffer.toString();
	}

	/**
	 * 添加'0'
	 *
	 * @param string
	 *            值长度
	 * @param length
	 *            总长度
	 * @param isleft
	 *            True为左边加空格,False为后补空格
	 * @return
	 */
	public static String addZero(String string, int length, boolean isleft) {
		if(TextUtils.isEmpty(string)){
			string = "";
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length - string.length(); i++) {
			buffer.append("0");
		}
		if (isleft)
			return buffer.append(string).toString();
		else
			return string + buffer.toString();
	}

	/**
	 * wangrongchao 增加判断转换大金额 2016.03.28
	 *
	 * @param
	 * @return
	 */
	public static String LongToAmt(long amt) {

		String StrTrace = "" + amt;
		String str1;
		String str2;
		if (StrTrace.length() == 2)
			StrTrace = "0." + StrTrace;
		else if (StrTrace.length() == 1)
			StrTrace = "0.0" + StrTrace;
		else {
			str1 = StrTrace.substring(StrTrace.length() - 2, StrTrace.length());
			str2 = StrTrace.substring(0, StrTrace.length() - 2);
			StrTrace = str2 + "." + str1;
		}
		return StrTrace;
	}

	/**
	 * 获取Admin密码
	 * @return
	 */
	public static String getAdminPassword(){

		String date = DateUtil.getDateTime3(new Date());//20210628
		String contentStr = date.substring(2);
		int value = 0;
		for(int i=0; i<contentStr.length(); i++){
			value += Integer.parseInt(contentStr.substring(i, i+1));
		}//2+1+0+6+2+8 = 19;
		return contentStr+String.format("%02d", value);//21062819
	}

	/**
	 * 获取Debug Log密码
	 * @return
	 */
	public static String getDebugPassword(){

		String date = DateUtil.getDateTime3(new Date());//20210628
		String contentStr = date.substring(2);
		int value = Integer.parseInt(contentStr.substring(0, 2))
				+Integer.parseInt(contentStr.substring(2, 4))
				+Integer.parseInt(contentStr.substring(4, 6));
		//21+07+04 = 32;
		String valueStr = String.format("%02d", value);
		return valueStr+valueStr+valueStr;//323232
	}

	/**
	 * 填充
	 * @param sourceContent
	 * @param totalLen 长度
	 * @return
	 */
	public static String getFillContent(String sourceContent, int totalLen) {
		if (sourceContent.length() > totalLen) {
			return sourceContent;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(sourceContent);
		int fillLen = totalLen - sourceContent.length();
		for (int i = 0; i < fillLen; i++) {
			builder.append(" ");
		}
		return builder.toString();
	}
}

package com.fx.sorm.utils;

/**
 * ��װ���ַ������ò���
 * @author Administrator
 *
 */
public class StringUtils {
	
	/**
	 * ��Ŀ���ַ�������ĸΪ��д
	 * @param str Ŀ���ַ���
	 * @return ����ĸ��Ϊ��д���ַ���
	 */
	public static String firstChar2UpperCass(String str) {
		return str.toUpperCase().substring(0, 1) + str.substring(1);
	}

}

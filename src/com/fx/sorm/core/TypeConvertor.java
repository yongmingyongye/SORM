package com.fx.sorm.core;

/**
 * ����java�������ͺ����ݿ��������͵Ļ���ת��
 * @author Administrator
 *
 */
public interface TypeConvertor {
	
	/**
	 * �����ݿ���������ת��Ϊjava����������
	 * @param columnType ���ݿ��ֶε���������
	 * @return java����������
	 */
	public String databaseType2JavaType(String columnType);
	
	/**
	 * ��java��������ת��Ϊ���ݿ���������
	 * @param javaDataType java��������
	 * @return ���ݿ���������
	 */
	public String javaTypeToDatabaseType(String javaDataType);

}

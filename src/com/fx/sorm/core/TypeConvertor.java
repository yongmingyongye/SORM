package com.fx.sorm.core;

/**
 * 负责java数据类型和数据库数据类型的互相转换
 * @author Administrator
 *
 */
public interface TypeConvertor {
	
	/**
	 * 将数据库数据类型转换为java的数据类型
	 * @param columnType 数据库字段的数据类型
	 * @return java的数据类型
	 */
	public String databaseType2JavaType(String columnType);
	
	/**
	 * 将java数据类型转换为数据库数据类型
	 * @param javaDataType java数据类型
	 * @return 数据库数据类型
	 */
	public String javaTypeToDatabaseType(String javaDataType);

}

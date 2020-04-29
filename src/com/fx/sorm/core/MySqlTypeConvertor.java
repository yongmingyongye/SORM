package com.fx.sorm.core;

/**
 * mysql数据类型和java数据类型的转换
 * 
 * @author Administrator
 *
 */
public class MySqlTypeConvertor implements TypeConvertor {

	@Override
	public String databaseType2JavaType(String columnType) {
		// varchar-->String
		if ("varchar".equalsIgnoreCase(columnType) || "char".equalsIgnoreCase(columnType)) {
			return "String";
		} else if ("int".equalsIgnoreCase(columnType) || "tinyint".equalsIgnoreCase(columnType)
				|| "smallint".equalsIgnoreCase(columnType) || "integer".equalsIgnoreCase(columnType)) {
			return "Integer";
		} else if ("bigint".equalsIgnoreCase(columnType)) {
			return "Long";
		} else if ("double".equalsIgnoreCase(columnType) || "float".equalsIgnoreCase(columnType)) {
			return "Double";
		} else if ("clob".equalsIgnoreCase(columnType)) {
			return "java.sql.Clob";
		} else if ("blob".equalsIgnoreCase(columnType)) {
			return "java.sql.blob";
		} else if ("date".equalsIgnoreCase(columnType)) {
			return "java.sql.Date";
		} else if ("time".equalsIgnoreCase(columnType)) {
			return "java.sql.Time";
		} else if ("timestamp".equalsIgnoreCase(columnType)) {
			return "java.sql.Timestamp";
		} else if ("decimal".equalsIgnoreCase(columnType)) {
			return "java.math.BigDecimal";
		}
		return null;
	}

	@Override
	public String javaTypeToDatabaseType(String javaDataType) {
		// TODO Auto-generated method stub
		return null;
	}

}

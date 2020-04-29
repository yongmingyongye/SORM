package com.fx.sorm.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 封装了JDBC查询常用的操作
 * @author Administrator
 *
 */
public class JDBCUtils {
	
	/**
	 * 给SQL设置参数
	 * @param ps 预编译SQL语句对象
	 * @param params 参数数组
	 */
	public static void handleParams(PreparedStatement ps, Object[] params) {
		//给SQL设置参数
		if (null != params) {
			for (int i = 0; i < params.length; i++) {
				try {
					ps.setObject(i + 1, params[i]);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}

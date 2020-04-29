package com.fx.sorm.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * ��װ��JDBC��ѯ���õĲ���
 * @author Administrator
 *
 */
public class JDBCUtils {
	
	/**
	 * ��SQL���ò���
	 * @param ps Ԥ����SQL������
	 * @param params ��������
	 */
	public static void handleParams(PreparedStatement ps, Object[] params) {
		//��SQL���ò���
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

package com.fx.sorm.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fx.sorm.core.DBManager;

/**
 * ���ݿ����ӳ���
 * @author Administrator
 *
 */
public class DBConnPool {
	
	/**
	 * ���ӳض���
	 */
	private List<Connection> pool; 
	
	/**
	 * ���������
	 */
	private static final int POOL_MAX_SIZE = DBManager.getConf().getPoolMaxSize();
	
	/**
	 * ��С������
	 */
	private static final int POOL_MIN_SIZE = DBManager.getConf().getPoolMinSize();
	
	public DBConnPool() {
		initPool();
	}
	
	/**
	 * ��ʼ�����ӳأ�ʹ�����������ﵽ��Сֵ
	 */
	public void initPool() {
		if (null == pool) {
			pool = new ArrayList<>();
		}
		while (pool.size() < POOL_MIN_SIZE) {
			pool.add(DBManager.createConnection());
			System.out.println("��ʼ�����ӳأ�������������" + pool.size());
		}
	}
	
	/**
	 * �����ӳ���ȡ��һ������
	 * @return
	 */
	public synchronized Connection getConnection() {
		int last_index = pool.size() - 1;
		Connection conn = pool.get(last_index);
		pool.remove(last_index);
		return conn;
	}
	
	/**
	 * �ر�����
	 * ʵ���ǽ����ӷŻ����ӳ�
	 * @param conn
	 */
	public synchronized void close(Connection conn) {
		if (pool.size() >= POOL_MAX_SIZE) {
			try {
				if (null != conn) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			pool.add(conn);
		}
	}
	

}

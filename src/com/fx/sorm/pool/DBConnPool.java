package com.fx.sorm.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fx.sorm.core.DBManager;

/**
 * 数据库连接池类
 * @author Administrator
 *
 */
public class DBConnPool {
	
	/**
	 * 连接池对象
	 */
	private List<Connection> pool; 
	
	/**
	 * 最大连接数
	 */
	private static final int POOL_MAX_SIZE = DBManager.getConf().getPoolMaxSize();
	
	/**
	 * 最小连接数
	 */
	private static final int POOL_MIN_SIZE = DBManager.getConf().getPoolMinSize();
	
	public DBConnPool() {
		initPool();
	}
	
	/**
	 * 初始化连接池，使池中连接数达到最小值
	 */
	public void initPool() {
		if (null == pool) {
			pool = new ArrayList<>();
		}
		while (pool.size() < POOL_MIN_SIZE) {
			pool.add(DBManager.createConnection());
			System.out.println("初始化连接池，池中连接数：" + pool.size());
		}
	}
	
	/**
	 * 从连接池中取出一个连接
	 * @return
	 */
	public synchronized Connection getConnection() {
		int last_index = pool.size() - 1;
		Connection conn = pool.get(last_index);
		pool.remove(last_index);
		return conn;
	}
	
	/**
	 * 关闭连接
	 * 实际是将连接放回连接池
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

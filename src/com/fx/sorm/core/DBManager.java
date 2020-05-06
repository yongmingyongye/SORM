package com.fx.sorm.core;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.fx.sorm.bean.Configuration;
import com.fx.sorm.pool.DBConnPool;

/**
 * 根据配置信息，维持连接对象的管理（增加连接池对象）
 * 
 * @author Administrator
 *
 */
public class DBManager {

	//配置信息
	private static Configuration conf;
	//连接池对象
	private static DBConnPool pool = null;

	static {// 静态代码块
		Properties pros = new Properties();
		try {
			pros.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		conf = new Configuration();
		conf.setDriver(pros.getProperty("driver"));
		conf.setPoPackage(pros.getProperty("poPackage"));
		conf.setPwd(pros.getProperty("pwd"));
		conf.setSrcPath(pros.getProperty("srcPath"));
		conf.setUrl(pros.getProperty("url"));
		conf.setUser(pros.getProperty("user"));
		conf.setUsingDB(pros.getProperty("usingDB"));
		conf.setQueryClass(pros.getProperty("queryClass"));
		conf.setPoolMaxSize(Integer.valueOf(pros.getProperty("poolMaxSize")));
		conf.setPoolMinSize(Integer.valueOf(pros.getProperty("poolMinSize")));
		
		//加载TableContext
		System.out.println(TableContext.class);
		
	}

	/**
	 * 获得Connection
	 * @return
	 */
	public static Connection createConnection() {
		Connection conn = null;
		try {
			Class.forName(conf.getDriver());
			// 建立连接(连接对象内部包含了Socket对象，是一个远程连接，比较耗时)
			conn = DriverManager.getConnection(conf.getUrl(), conf.getUser(), conf.getPwd());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * 获得Connection
	 * @return
	 */
	public static Connection getConnection() {
		if (null == pool) {
			pool = new DBConnPool();
		}
		return pool.getConnection();
	}
	
	/**
	 * 返回Configuration对象
	 * @return
	 */
	public static Configuration getConf() {
		return conf;
	}

	public static void close(Closeable... closeables) {

		for (Closeable c : closeables) {
			try {
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 关闭传入的ResultSet、Statement、Connection连接
	 * @param rs
	 * @param ps
	 * @param conn
	 */
	public static void close(ResultSet rs, Statement ps, Connection conn) {

		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (conn != null) {
			pool.close(conn);
		}

	}

	/**
	 * 关闭Statement、Connection连接
	 * @param ps
	 * @param conn
	 */
	public static void close(Statement ps, Connection conn) {

		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (conn != null) {
			pool.close(conn);
		}

	}

	/**
	 * 关闭Connection
	 * @param conn
	 */
	public static void close(Connection conn) {

		if (conn != null) {
			pool.close(conn);
		}

	}

}

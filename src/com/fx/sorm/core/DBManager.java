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
 * ����������Ϣ��ά�����Ӷ���Ĺ����������ӳض���
 * 
 * @author Administrator
 *
 */
public class DBManager {

	//������Ϣ
	private static Configuration conf;
	//���ӳض���
	private static DBConnPool pool = null;

	static {// ��̬�����
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
		
		//����TableContext
		System.out.println(TableContext.class);
		
	}

	/**
	 * ���Connection
	 * @return
	 */
	public static Connection createConnection() {
		Connection conn = null;
		try {
			Class.forName(conf.getDriver());
			// ��������(���Ӷ����ڲ�������Socket������һ��Զ�����ӣ��ȽϺ�ʱ)
			conn = DriverManager.getConnection(conf.getUrl(), conf.getUser(), conf.getPwd());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * ���Connection
	 * @return
	 */
	public static Connection getConnection() {
		if (null == pool) {
			pool = new DBConnPool();
		}
		return pool.getConnection();
	}
	
	/**
	 * ����Configuration����
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
	 * �رմ����ResultSet��Statement��Connection����
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
	 * �ر�Statement��Connection����
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
	 * �ر�Connection
	 * @param conn
	 */
	public static void close(Connection conn) {

		if (conn != null) {
			pool.close(conn);
		}

	}

}

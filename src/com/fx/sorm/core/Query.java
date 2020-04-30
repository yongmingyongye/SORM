package com.fx.sorm.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fx.sorm.bean.ColumnInfo;
import com.fx.sorm.bean.TableInfo;
import com.fx.sorm.utils.JDBCUtils;
import com.fx.sorm.utils.ReflectUtils;

/**
 * 负责查询（对外提供服务的核心类）
 * 
 * @author Administrator
 *
 */
public abstract class Query implements Cloneable {

	/**
	 * 采用模板方法模式将JDBC操作封装为模板，便于重用
	 * @param sql sql语句
	 * @param params sql参数
	 * @param clazz 要封装到的java类
	 * @param back CallBack实现类，用于回调
	 * @return
	 */
	public Object executeQueryTemplate(String sql, Object[] params, Class<?> clazz, CallBack back) {
		Connection conn = DBManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			JDBCUtils.handleParams(ps, params);
			System.out.println(ps);
			rs = ps.executeQuery();
			return back.doExecute(conn, ps, rs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			DBManager.close(rs, ps, conn);
		}
	}

	/**
	 * 直接执行一个DML语句(增删改)
	 * 
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数
	 * @return 执行SQL语句后影响记录的行数
	 */
	public Integer executeDML(String sql, Object[] params) {
		Connection conn = DBManager.getConnection();
		int count = 0;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			// 给SQL设置参数
			JDBCUtils.handleParams(ps, params);
			count = ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBManager.close(ps, conn);
		}
		return count;
	}

	/**
	 * 将一个对象存储到数据库中 把对象中不为null的属性的数据插入到数据库中
	 * 
	 * @param obj
	 */
	public void insert(Object obj) {
		// obj --> 表 insert into 表名 (field1, field2, ...) values (?, ?, ...)
		Class<?> c = obj.getClass();
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		Field[] fields = c.getDeclaredFields();
		StringBuilder sql = new StringBuilder("insert into ").append(tableInfo.getTname()).append(" (");
		int countNotNullField = 0;// 计算不为null的属性个数
		List<Object> params = new ArrayList<Object>(); // 存储SQL的参数对象
		for (Field f : fields) {
			String fieldName = f.getName();
			Object fieldValue = ReflectUtils.invokeGet(fieldName, obj);
			if (null != fieldValue) {
				sql.append(fieldName).append(", ");
				countNotNullField++;
				params.add(fieldValue);
			}
		}
		sql.setCharAt(sql.lastIndexOf(","), ')');
		sql.append("values (");
		for (int i = 0; i < countNotNullField; i++) {
			sql.append("?, ");
		}
		sql.setCharAt(sql.lastIndexOf(","), ')');
		System.out.println(sql.toString());
		executeDML(sql.toString(), params.toArray());

	}

	/**
	 * 删除clazz表示类对应的表中的记录（指定主键值id的记录）
	 * 
	 * @param clazz
	 *            根表对应的类的Class对象
	 * @param id
	 *            主键的值
	 */
	@SuppressWarnings("rawtypes")
	public void delete(Class clazz, Object id) {
		// Emp.class,2 --> delete from emp where id = 2
		// 通过Class对象找TableInfo
		TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
		StringBuilder sql = new StringBuilder("delete from ");
		sql.append(tableInfo.getTname()).append(" where ").append(onlyPriKey.getName()).append(" = ? ");
		executeDML(sql.toString(), new Object[] { id });
	}

	/**
	 * 删除对象在数据库中对应的记录（对象所在类对应到表，对象的主键的值对应到记录）
	 * 
	 * @param obj
	 */
	public void delete(Object obj) {
		Class<?> c = obj.getClass();
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();// 获得主键

		// 通过反射调用属性对应的get或set方法
		Object priKeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(), obj);

		delete(c, priKeyValue);

	}

	/**
	 * 更新对象对应的记录，只更新指定的字段的值
	 * 
	 * @param obj
	 *            所要更新的对象
	 * @param fieldNames
	 *            更新的属性列表
	 * @return 执行SQL语句后影响的记录的行数
	 */
	public Integer update(Object obj, String[] fieldNames) {
		// obj{"uname","pwd"} --> update 表名 set uanme = ?, pwd = ? where id = ?
		Class<?> c = obj.getClass();
		List<Object> params = new ArrayList<>();
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		StringBuilder sql = new StringBuilder("update ").append(tableInfo.getTname()).append(" set ");
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
		for (String fname : fieldNames) {
			Object fvalue = ReflectUtils.invokeGet(fname, obj);
			sql.append(fname).append(" = ?, ");
			params.add(fvalue);
		}
		sql.setCharAt(sql.lastIndexOf(","), ' ');
		sql.append("where ").append(onlyPriKey.getName()).append(" = ?");
		params.add(ReflectUtils.invokeGet(onlyPriKey.getName(), obj));
		return executeDML(sql.toString(), params.toArray());
	}

	/**
	 * 查询返回多行记录，并将每行记录封装到clazz指定的类的对象中
	 * 
	 * @param sql
	 *            查询语句
	 * @param clazz
	 *            封装数据的Javabean类的class对象
	 * @param params
	 *            SQL的参数
	 * @return 查询到的结果
	 */
	@SuppressWarnings("all")
	public List queryRows(final String sql, final Class<?> clazz, final Object[] params) {

		return (List) executeQueryTemplate(sql, params, clazz, new CallBack() {

			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				List list = null;
				try {
					ResultSetMetaData metaData = rs.getMetaData();
					// 多行
					while (rs.next()) {
						if (null == list) {
							list = new ArrayList();
						}
						Object rowObj = clazz.getConstructor().newInstance();// 调用javabean的无参构造器
						// 多列,metaData.getColumnCount()查询结果有几列
						for (int i = 0; i < metaData.getColumnCount(); i++) {
							String columnName = metaData.getColumnLabel(i + 1);
							Object columnValue = rs.getObject(i + 1);
							// 调用rowObj对象的set方法，将columnValue的值设置到对应的属性中去
							ReflectUtils.invokeSet(columnName, columnValue, rowObj);
						}
						list.add(rowObj);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return list;
			}

		});
	}

	/**
	 * 查询返回一行记录，并将该记录封装到clazz指定的类的对象中
	 * 
	 * @param sql
	 *            查询语句
	 * @param clazz
	 *            封装数据的Javabean类的class对象
	 * @param params
	 *            SQL的参数
	 * @return 查询到的结果
	 */
	@SuppressWarnings("rawtypes")
	public Object queryUniqueRow(String sql, Class<?> clazz, Object[] params) {
		List rows = queryRows(sql, clazz, params);
		return (null != rows && rows.size() > 0) ? rows.get(0) : null;
	}

	/**
	 * 查询返回一个值（一行一列），并将该值返回
	 * 
	 * @param sql
	 *            查询语句
	 * @param params
	 *            SQL的参数
	 * @return 查询到的结果
	 */
	public Object queryValue(String sql, Object[] params) {
		/*Connection conn = DBManager.getConnection();
		Object value = null;// 存放查询结果的对象
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			JDBCUtils.handleParams(ps, params);
			System.out.println(ps);
			rs = ps.executeQuery();
			// ResultSetMetaData metaData = rs.getMetaData();
			// 多行
			while (rs.next()) {
				value = rs.getObject(1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBManager.close(rs, ps, conn);
		}
		return value;*/
		return executeQueryTemplate(sql, params, null, new CallBack() {

			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				Object value = null;
				try {
					while (rs.next()) {
						value = rs.getObject(1);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return value;
			}
			
		});
	}

	/**
	 * 查询返回一个数字（一行一列），并将该值返回
	 * 
	 * @param sql
	 *            查询语句
	 * @param params
	 *            SQL的参数
	 * @return 查询到的结果
	 */
	public Number queryNumber(String sql, Object[] params) {
		return (Number) queryValue(sql, params);
	}

	/**
	 * 分页查询
	 * 
	 * @param pageNum
	 *            第几页数据
	 * @param size
	 *            每页显示多少记录
	 * @return
	 */
	public abstract Object queryPagenate(int pageNum, int size);
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}

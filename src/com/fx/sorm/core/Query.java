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
 * �����ѯ�������ṩ����ĺ����ࣩ
 * 
 * @author Administrator
 *
 */
public abstract class Query implements Cloneable {

	/**
	 * ����ģ�巽��ģʽ��JDBC������װΪģ�壬��������
	 * @param sql sql���
	 * @param params sql����
	 * @param clazz Ҫ��װ����java��
	 * @param back CallBackʵ���࣬���ڻص�
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
	 * ֱ��ִ��һ��DML���(��ɾ��)
	 * 
	 * @param sql
	 *            sql���
	 * @param params
	 *            ����
	 * @return ִ��SQL����Ӱ���¼������
	 */
	public Integer executeDML(String sql, Object[] params) {
		Connection conn = DBManager.getConnection();
		int count = 0;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			// ��SQL���ò���
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
	 * ��һ������洢�����ݿ��� �Ѷ����в�Ϊnull�����Ե����ݲ��뵽���ݿ���
	 * 
	 * @param obj
	 */
	public void insert(Object obj) {
		// obj --> �� insert into ���� (field1, field2, ...) values (?, ?, ...)
		Class<?> c = obj.getClass();
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		Field[] fields = c.getDeclaredFields();
		StringBuilder sql = new StringBuilder("insert into ").append(tableInfo.getTname()).append(" (");
		int countNotNullField = 0;// ���㲻Ϊnull�����Ը���
		List<Object> params = new ArrayList<Object>(); // �洢SQL�Ĳ�������
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
	 * ɾ��clazz��ʾ���Ӧ�ı��еļ�¼��ָ������ֵid�ļ�¼��
	 * 
	 * @param clazz
	 *            �����Ӧ�����Class����
	 * @param id
	 *            ������ֵ
	 */
	@SuppressWarnings("rawtypes")
	public void delete(Class clazz, Object id) {
		// Emp.class,2 --> delete from emp where id = 2
		// ͨ��Class������TableInfo
		TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
		StringBuilder sql = new StringBuilder("delete from ");
		sql.append(tableInfo.getTname()).append(" where ").append(onlyPriKey.getName()).append(" = ? ");
		executeDML(sql.toString(), new Object[] { id });
	}

	/**
	 * ɾ�����������ݿ��ж�Ӧ�ļ�¼�������������Ӧ���������������ֵ��Ӧ����¼��
	 * 
	 * @param obj
	 */
	public void delete(Object obj) {
		Class<?> c = obj.getClass();
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();// �������

		// ͨ������������Զ�Ӧ��get��set����
		Object priKeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(), obj);

		delete(c, priKeyValue);

	}

	/**
	 * ���¶����Ӧ�ļ�¼��ֻ����ָ�����ֶε�ֵ
	 * 
	 * @param obj
	 *            ��Ҫ���µĶ���
	 * @param fieldNames
	 *            ���µ������б�
	 * @return ִ��SQL����Ӱ��ļ�¼������
	 */
	public Integer update(Object obj, String[] fieldNames) {
		// obj{"uname","pwd"} --> update ���� set uanme = ?, pwd = ? where id = ?
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
	 * ��ѯ���ض��м�¼������ÿ�м�¼��װ��clazzָ������Ķ�����
	 * 
	 * @param sql
	 *            ��ѯ���
	 * @param clazz
	 *            ��װ���ݵ�Javabean���class����
	 * @param params
	 *            SQL�Ĳ���
	 * @return ��ѯ���Ľ��
	 */
	@SuppressWarnings("all")
	public List queryRows(final String sql, final Class<?> clazz, final Object[] params) {

		return (List) executeQueryTemplate(sql, params, clazz, new CallBack() {

			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				List list = null;
				try {
					ResultSetMetaData metaData = rs.getMetaData();
					// ����
					while (rs.next()) {
						if (null == list) {
							list = new ArrayList();
						}
						Object rowObj = clazz.getConstructor().newInstance();// ����javabean���޲ι�����
						// ����,metaData.getColumnCount()��ѯ����м���
						for (int i = 0; i < metaData.getColumnCount(); i++) {
							String columnName = metaData.getColumnLabel(i + 1);
							Object columnValue = rs.getObject(i + 1);
							// ����rowObj�����set��������columnValue��ֵ���õ���Ӧ��������ȥ
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
	 * ��ѯ����һ�м�¼�������ü�¼��װ��clazzָ������Ķ�����
	 * 
	 * @param sql
	 *            ��ѯ���
	 * @param clazz
	 *            ��װ���ݵ�Javabean���class����
	 * @param params
	 *            SQL�Ĳ���
	 * @return ��ѯ���Ľ��
	 */
	@SuppressWarnings("rawtypes")
	public Object queryUniqueRow(String sql, Class<?> clazz, Object[] params) {
		List rows = queryRows(sql, clazz, params);
		return (null != rows && rows.size() > 0) ? rows.get(0) : null;
	}

	/**
	 * ��ѯ����һ��ֵ��һ��һ�У���������ֵ����
	 * 
	 * @param sql
	 *            ��ѯ���
	 * @param params
	 *            SQL�Ĳ���
	 * @return ��ѯ���Ľ��
	 */
	public Object queryValue(String sql, Object[] params) {
		/*Connection conn = DBManager.getConnection();
		Object value = null;// ��Ų�ѯ����Ķ���
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			JDBCUtils.handleParams(ps, params);
			System.out.println(ps);
			rs = ps.executeQuery();
			// ResultSetMetaData metaData = rs.getMetaData();
			// ����
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
	 * ��ѯ����һ�����֣�һ��һ�У���������ֵ����
	 * 
	 * @param sql
	 *            ��ѯ���
	 * @param params
	 *            SQL�Ĳ���
	 * @return ��ѯ���Ľ��
	 */
	public Number queryNumber(String sql, Object[] params) {
		return (Number) queryValue(sql, params);
	}

	/**
	 * ��ҳ��ѯ
	 * 
	 * @param pageNum
	 *            �ڼ�ҳ����
	 * @param size
	 *            ÿҳ��ʾ���ټ�¼
	 * @return
	 */
	public abstract Object queryPagenate(int pageNum, int size);
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}

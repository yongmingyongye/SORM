package com.fx.sorm.core;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.fx.po.Emp;
import com.fx.sorm.bean.ColumnInfo;
import com.fx.sorm.bean.TableInfo;
import com.fx.sorm.utils.JDBCUtils;
import com.fx.sorm.utils.ReflectUtils;
import com.fx.sorm.vo.EmpVO;

/**
 * 负责针对MySQL的操作
 * @author Administrator
 *
 */
@SuppressWarnings("all")
public class MySqlQuery implements Query {
	
	public static void testDML() {
		Emp e = new Emp();
		e.setId(4);
		e.setEmpname("赵六");
		e.setSalary(new BigDecimal("15000"));
		e.setAge(26);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			e.setBirthday(new java.sql.Date(format.parse("1994-09-01").getTime()));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		new MySqlQuery().update(e, new String[] {"salary", "age", "birthday"});
	}
	
	public static void testQueryRows() {
		List<Emp> rows = new MySqlQuery().queryRows("select id, empname, age, salary from emp where age > ? and salary < ?", 
				Emp.class, new Object[] {Integer.valueOf(20), new BigDecimal("15000")});
		System.out.println(rows);
		
		String sql = "select e.id, e.empname, e.salary + e.bonus 'xinshui', e.age, d.dname 'deptName', d.address 'deptAddress' from emp e " + 
				"join dept d on e.deptId = d.id";
		List<EmpVO> rows2 = new MySqlQuery().queryRows(sql, EmpVO.class, new Object[] {});
		for (Object obj : rows2) {
			EmpVO e = (EmpVO) obj;
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) {
		Object value = new MySqlQuery().queryValue("select count(*) from emp where salary > ?", new Object[] {5000});
		System.out.println(value);
	}

	@Override
	public Integer executeDML(String sql, Object[] params) {
		Connection conn = DBManager.getConnection();
		int count = 0;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			//给SQL设置参数
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

	@Override
	public void insert(Object obj) {
		//obj --> 表       insert into 表名   (field1, field2, ...) values (?, ?, ...)
		Class<?> c = obj.getClass();
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		Field[] fields = c.getDeclaredFields();
		StringBuilder sql = new StringBuilder("insert into ").append(tableInfo.getTname()).append(" (");
		int countNotNullField = 0;//计算不为null的属性个数
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

	@Override
	public void delete(Class<?> clazz, Object id) {
		//Emp.class,2 --> delete from emp where id = 2
		//通过Class对象找TableInfo
		TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
		StringBuilder sql = new StringBuilder("delete from ");
		sql.append(tableInfo.getTname()).append(" where ").append(onlyPriKey.getName()).append(" = ? ");
		executeDML(sql.toString(), new Object[] {id});
	}

	@Override
	public void delete(Object obj) {
		Class<?> c = obj.getClass();
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();//获得主键
		
		//通过反射调用属性对应的get或set方法
		Object priKeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(), obj);
		
		delete(c, priKeyValue);
		
	}

	@Override
	public Integer update(Object obj, String[] fieldNames) {
		//obj{"uname","pwd"} --> update 表名  set  uanme = ?, pwd = ? where id = ?
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

	@Override
	public List queryRows(String sql, Class<?> clazz, Object[] params) {
		Connection conn = DBManager.getConnection();
		List<Object> list = null;//存放查询结果的容器
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			JDBCUtils.handleParams(ps, params);
			System.out.println(ps);
			rs = ps.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			//多行
			while (rs.next()) {
				if (null == list) {
					 list = new ArrayList<>();
				}
				Object rowObj = clazz.getConstructor().newInstance();//调用javabean的无参构造器
				//多列,metaData.getColumnCount()查询结果有几列
				for (int i = 0; i < metaData.getColumnCount(); i++) {
					String columnName = metaData.getColumnLabel(i + 1);
					Object columnValue = rs.getObject(i + 1);
					//调用rowObj对象的set方法，将columnValue的值设置到对应的属性中去
					ReflectUtils.invokeSet(columnName, columnValue, rowObj);
				}
				list.add(rowObj);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBManager.close(rs, ps, conn);
		}
		return list;
	}

	@Override
	public Object queryUniqueRow(String sql, Class<?> clazz, Object[] params) {
		List rows = queryRows(sql, clazz, params);
		return (null != rows && rows.size() > 0) ? rows.get(0) : null;
	}

	@Override
	public Object queryValue(String sql, Object[] params) {
		Connection conn = DBManager.getConnection();
		Object value = null;//存放查询结果的对象
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			JDBCUtils.handleParams(ps, params);
			System.out.println(ps);
			rs = ps.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			//多行
			while (rs.next()) {
				value = rs.getObject(1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBManager.close(rs, ps, conn);
		}
		return value;
	}

	@Override
	public Number queryNumber(String sql, Object[] params) {
		return (Number) queryValue(sql, params);
	}

}

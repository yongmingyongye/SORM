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
public class MySqlQuery extends Query {
	
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
//		testQueryRows();
	}

	@Override
	public Object queryPagenate(int pageNum, int size) {
		// TODO Auto-generated method stub
		return null;
	}


}

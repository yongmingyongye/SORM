package com.fx.test;

import java.util.List;

import com.fx.sorm.core.Query;
import com.fx.sorm.core.QueryFactory;
import com.fx.sorm.vo.EmpVO;

public class TestConnectionPool {
	
	public static void test01() {
		String sql = "select e.id, e.empname, e.salary + e.bonus 'xinshui', e.age, d.dname 'deptName', d.address 'deptAddress' from emp e " + 
				"join dept d on e.deptId = d.id";
		Query query = QueryFactory.getInstance().createQuery();
		List<EmpVO> rows2 = query.queryRows(sql, EmpVO.class, new Object[] {});
		for (Object obj : rows2) {
			EmpVO e = (EmpVO) obj;
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) {
		Long start = System.currentTimeMillis();
		for (int i = 0; i < 3000; i++) {
			test01();
		}
		Long end = System.currentTimeMillis();
		System.out.println(end - start);
		
		//不加入连接池耗时：10425ms
		//加入连接池耗时：2633ms
	}

}

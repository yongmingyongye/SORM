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
	
	@Override
	public Object queryPagenate(int pageNum, int size) {
		// TODO Auto-generated method stub
		return null;
	}


}

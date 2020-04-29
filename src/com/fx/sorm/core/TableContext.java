package com.fx.sorm.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fx.sorm.bean.ColumnInfo;
import com.fx.sorm.bean.TableInfo;
import com.fx.sorm.utils.JavaFileUtils;
import com.fx.sorm.utils.StringUtils;

/**
 * 负责获取管理数据库所有表结构和类结构的关系，可以根据表结构生成类结构
 * @author Administrator
 *
 */
public class TableContext {
	
	/**
	 * 表名为key，表信息为value
	 */
	public static Map<String, TableInfo> tables = new HashMap<>();
	
	/**
	 * 将po的class对象和表信息对象关联起来，便于重用
	 */
	public static Map<Class<?>, TableInfo> poClassTableMap = new HashMap<>();
	
	private TableContext() {}
	
	static {
		try {
			//初始化获得表的信息
			Connection conn = DBManager.getConnection();
			DatabaseMetaData dbmd = conn.getMetaData();
			
			ResultSet tableRet = dbmd.getTables(null, "%", "%", new String[] {"TABLE"});
			while (tableRet.next()) {
				String tableName = (String) tableRet.getObject("TABLE_NAME");
				TableInfo ti = new TableInfo(tableName, new HashMap<String, ColumnInfo>(), new ArrayList<ColumnInfo>());
				tables.put(tableName, ti);
				//查询表中的所有字段
				ResultSet set = dbmd.getColumns(null, "%", tableName, "%");
				while (set.next()) {
					ColumnInfo ci = new ColumnInfo(set.getString("COLUMN_NAME"), set.getString("TYPE_NAME"), 0);
					ti.getColumns().put(set.getString("COLUMN_NAME"), ci);
				}
				//查询t_user表中的主键
				ResultSet set2 = dbmd.getPrimaryKeys(null, "%", tableName);
				while (set2.next()) {
					ColumnInfo ci2 = ti.getColumns().get(set2.getObject("COLUMN_NAME"));
					ci2.setKeyType(1);//设置为主键类型
					ti.getPrikeys().add(ci2);
				}
				if (ti.getPrikeys().size() > 0) {// 取唯一主键。方便使用，如果是联合主键则为空
					ti.setOnlyPriKey(ti.getPrikeys().get(0));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//每次加载这个类时更新类结构
		updateJavaPOFile();
		
		//加载po包下面的类，便于重用提高效率
		loadPOTable();
	}
	
	public static Map<String, TableInfo> getTableInfos() {
		return tables;
	}
	
	/**
	 * 根据表结构，更新配置的包下面java类
	 */
	public static void updateJavaPOFile() {
		Map<String, TableInfo> tables = TableContext.tables;
		for (TableInfo t : tables.values()) {
			JavaFileUtils.createJavaPOFile(t, new MySqlTypeConvertor());
		}
	}
	
	/**
	 * 加载po包下的类
	 */
	public static void loadPOTable() {
		for (TableInfo tableInfo : tables.values()) {
			Class<?> c;
			try {
				c = Class.forName(DBManager.getConf().getPoPackage() + "." + StringUtils.firstChar2UpperCass(tableInfo.getTname()));
				poClassTableMap.put(c, tableInfo);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
//	public static void main(String[] args) {
//		Map<String, TableInfo> tables = getTableInfos();
//		System.out.println(tables);
//	}

}

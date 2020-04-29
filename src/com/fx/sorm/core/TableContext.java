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
 * �����ȡ�������ݿ����б�ṹ����ṹ�Ĺ�ϵ�����Ը��ݱ�ṹ������ṹ
 * @author Administrator
 *
 */
public class TableContext {
	
	/**
	 * ����Ϊkey������ϢΪvalue
	 */
	public static Map<String, TableInfo> tables = new HashMap<>();
	
	/**
	 * ��po��class����ͱ���Ϣ���������������������
	 */
	public static Map<Class<?>, TableInfo> poClassTableMap = new HashMap<>();
	
	private TableContext() {}
	
	static {
		try {
			//��ʼ����ñ����Ϣ
			Connection conn = DBManager.getConnection();
			DatabaseMetaData dbmd = conn.getMetaData();
			
			ResultSet tableRet = dbmd.getTables(null, "%", "%", new String[] {"TABLE"});
			while (tableRet.next()) {
				String tableName = (String) tableRet.getObject("TABLE_NAME");
				TableInfo ti = new TableInfo(tableName, new HashMap<String, ColumnInfo>(), new ArrayList<ColumnInfo>());
				tables.put(tableName, ti);
				//��ѯ���е������ֶ�
				ResultSet set = dbmd.getColumns(null, "%", tableName, "%");
				while (set.next()) {
					ColumnInfo ci = new ColumnInfo(set.getString("COLUMN_NAME"), set.getString("TYPE_NAME"), 0);
					ti.getColumns().put(set.getString("COLUMN_NAME"), ci);
				}
				//��ѯt_user���е�����
				ResultSet set2 = dbmd.getPrimaryKeys(null, "%", tableName);
				while (set2.next()) {
					ColumnInfo ci2 = ti.getColumns().get(set2.getObject("COLUMN_NAME"));
					ci2.setKeyType(1);//����Ϊ��������
					ti.getPrikeys().add(ci2);
				}
				if (ti.getPrikeys().size() > 0) {// ȡΨһ����������ʹ�ã����������������Ϊ��
					ti.setOnlyPriKey(ti.getPrikeys().get(0));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//ÿ�μ��������ʱ������ṹ
		updateJavaPOFile();
		
		//����po��������࣬�����������Ч��
		loadPOTable();
	}
	
	public static Map<String, TableInfo> getTableInfos() {
		return tables;
	}
	
	/**
	 * ���ݱ�ṹ���������õİ�����java��
	 */
	public static void updateJavaPOFile() {
		Map<String, TableInfo> tables = TableContext.tables;
		for (TableInfo t : tables.values()) {
			JavaFileUtils.createJavaPOFile(t, new MySqlTypeConvertor());
		}
	}
	
	/**
	 * ����po���µ���
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

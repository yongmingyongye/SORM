package com.fx.sorm.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fx.sorm.bean.ColumnInfo;
import com.fx.sorm.bean.JavaFieldGetSet;
import com.fx.sorm.bean.TableInfo;
import com.fx.sorm.core.DBManager;
import com.fx.sorm.core.MySqlTypeConvertor;
import com.fx.sorm.core.TableContext;
import com.fx.sorm.core.TypeConvertor;

/**
 * ��װ��Java�ļ���Դ���룩���ò���
 * 
 * @author Administrator
 *
 */
public class JavaFileUtils {

	/**
	 * �����ֶ���Ϣ����java������Ϣ���磺varchar username --> private String
	 * username;�Լ���Ӧ��set��get����
	 * 
	 * @param column
	 * @param convertor
	 * @return
	 */
	public static JavaFieldGetSet createFieldGetSetSRC(ColumnInfo column, TypeConvertor convertor) {

		JavaFieldGetSet jfgs = new JavaFieldGetSet();
		String javaFieldType = convertor.databaseType2JavaType(column.getDataType());
		jfgs.setFieldInfo("\tprivate " + javaFieldType + " " + column.getName() + ";\n");
		// public String getUsername() {return username;}
		// ����get������Դ����
		StringBuilder getSrc = new StringBuilder();
		getSrc.append(
				"\tpublic " + javaFieldType + " get" + StringUtils.firstChar2UpperCass(column.getName()) + "() {\n");
		getSrc.append("\t\treturn " + column.getName() + ";\n").append("\t}\n");
		jfgs.setGetInfo(getSrc.toString());

		// public void setUsername(String username) {this.username = username;}
		// ����set������Դ����
		StringBuilder setSrc = new StringBuilder();
		setSrc.append("\tpublic void set" + StringUtils.firstChar2UpperCass(column.getName()) + "(")
				.append(javaFieldType).append(" ").append(column.getName()).append(") {\n");
		setSrc.append("\t\tthis." + column.getName() + "=" + column.getName() + ";\n").append("\t}\n");
		jfgs.setSetInfo(setSrc.toString());
		return jfgs;
	}
	
	/**
	 * ���ݱ���Ϣ����java���Դ����
	 * @param tableInfo ����Ϣ
	 * @param convertor ��������ת����
	 * @return java���Դ����
	 */
	public static String createJavaSrc(TableInfo tableInfo, TypeConvertor convertor) {
		
		Map<String, ColumnInfo> columns = tableInfo.getColumns();
		List<JavaFieldGetSet> javaFields = new ArrayList<JavaFieldGetSet>();
		for (ColumnInfo c : columns.values()) {
			javaFields.add(createFieldGetSetSRC(c, convertor));
		}
		
		StringBuilder src = new StringBuilder();
		//����package���
		src.append("package ").append(DBManager.getConf().getPoPackage()).append(";\n\n");
		
		//����import���
		src.append("import java.sql.*;\n").append("import java.util.*;\n\n");
		
		//�������������
		src.append("public class ").append(StringUtils.firstChar2UpperCass(tableInfo.getTname())).append(" {\n\n");
		
		//���������б�
		for (JavaFieldGetSet f : javaFields) {
			src.append(f.getFieldInfo());
		}
		src.append("\n\n");
		
		//����get�����б�
		for (JavaFieldGetSet f : javaFields) {
			src.append(f.getGetInfo());
		}
		src.append("\n\n");
		
		//����set�����б�
		for (JavaFieldGetSet f : javaFields) {
			src.append(f.getSetInfo());
		}
		src.append("\n\n");
		
		//���������
		src.append("}\n");
		return src.toString();
	}
	
	public static void createJavaPOFile(TableInfo tableInfo, TypeConvertor typeConvertor) {
		String src = createJavaSrc(tableInfo, typeConvertor);
		
		String srcPath = DBManager.getConf().getSrcPath() + "\\";
		String packagePath = DBManager.getConf().getPoPackage().replace(".", "\\") + "\\";
		String fileName = StringUtils.firstChar2UpperCass(tableInfo.getTname()) + ".java";
		String path = srcPath + packagePath;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(path + fileName));
			bw.write(src);
			System.out.println("������" + tableInfo.getTname() + "��Ӧ��java�ࣺ" + StringUtils.firstChar2UpperCass(tableInfo.getTname()) + ".java");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
//		ColumnInfo ci = new ColumnInfo("username", "int", 0);
//		JavaFieldGetSet f = createFieldGetSetSRC(ci, new MySqlTypeConvertor());
//		System.out.println(f);
		Map<String, TableInfo> tables = TableContext.tables;
		for (TableInfo t : tables.values()) {
			createJavaPOFile(t, new MySqlTypeConvertor());
		}
	}

}

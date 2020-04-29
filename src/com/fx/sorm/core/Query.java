package com.fx.sorm.core;

import java.util.List;

/**
 * 负责查询（对外提供服务的核心类）
 * @author Administrator
 *
 */
public interface Query {
	
	/**
	 * 直接执行一个DML语句(增删改)
	 * @param sql sql语句
	 * @param params 参数
	 * @return 执行SQL语句后影响记录的行数
	 */
	public Integer executeDML(String sql, Object[] params);
	
	/**
	 * 将一个对象存储到数据库中
	 * 把对象中不为null的属性的数据插入到数据库中
	 * @param obj
	 */
	public void insert(Object obj);
	
	/**
	 * 删除clazz表示类对应的表中的记录（指定主键值id的记录）
	 * @param clazz 根表对应的类的Class对象
	 * @param id 主键的值
	 */
	public void delete(Class<?> clazz, Object id);

	/**
	 * 删除对象在数据库中对应的记录（对象所在类对应到表，对象的主键的值对应到记录）
	 * @param obj
	 */
	public void delete(Object obj);
	
	/**
	 * 更新对象对应的记录，只更新指定的字段的值
	 * @param obj 所要更新的对象
	 * @param fieldNames 更新的属性列表
	 * @return 执行SQL语句后影响的记录的行数
	 */
	public Integer update(Object obj, String[] fieldNames);

	/**
	 * 查询返回多行记录，并将每行记录封装到clazz指定的类的对象中
	 * @param sql 查询语句
	 * @param clazz 封装数据的Javabean类的class对象
	 * @param params SQL的参数
	 * @return 查询到的结果
	 */
	public List<?> queryRows(String sql, Class<?> clazz, Object[] params);
	
	/**
	 * 查询返回一行记录，并将该记录封装到clazz指定的类的对象中
	 * @param sql 查询语句
	 * @param clazz 封装数据的Javabean类的class对象
	 * @param params SQL的参数
	 * @return 查询到的结果
	 */
	public Object queryUniqueRow(String sql, Class<?> clazz, Object[] params);
	
	/**
	 * 查询返回一个值（一行一列），并将该值返回
	 * @param sql 查询语句
	 * @param params SQL的参数
	 * @return 查询到的结果
	 */
	public Object queryValue(String sql, Object[] params);
	
	/**
	 * 查询返回一个数字（一行一列），并将该值返回
	 * @param sql 查询语句
	 * @param params SQL的参数
	 * @return 查询到的结果
	 */
	public Number queryNumber(String sql, Object[] params);

}

package com.fx.sorm.core;

import java.util.List;

/**
 * �����ѯ�������ṩ����ĺ����ࣩ
 * @author Administrator
 *
 */
public interface Query {
	
	/**
	 * ֱ��ִ��һ��DML���(��ɾ��)
	 * @param sql sql���
	 * @param params ����
	 * @return ִ��SQL����Ӱ���¼������
	 */
	public Integer executeDML(String sql, Object[] params);
	
	/**
	 * ��һ������洢�����ݿ���
	 * �Ѷ����в�Ϊnull�����Ե����ݲ��뵽���ݿ���
	 * @param obj
	 */
	public void insert(Object obj);
	
	/**
	 * ɾ��clazz��ʾ���Ӧ�ı��еļ�¼��ָ������ֵid�ļ�¼��
	 * @param clazz �����Ӧ�����Class����
	 * @param id ������ֵ
	 */
	public void delete(Class<?> clazz, Object id);

	/**
	 * ɾ�����������ݿ��ж�Ӧ�ļ�¼�������������Ӧ���������������ֵ��Ӧ����¼��
	 * @param obj
	 */
	public void delete(Object obj);
	
	/**
	 * ���¶����Ӧ�ļ�¼��ֻ����ָ�����ֶε�ֵ
	 * @param obj ��Ҫ���µĶ���
	 * @param fieldNames ���µ������б�
	 * @return ִ��SQL����Ӱ��ļ�¼������
	 */
	public Integer update(Object obj, String[] fieldNames);

	/**
	 * ��ѯ���ض��м�¼������ÿ�м�¼��װ��clazzָ������Ķ�����
	 * @param sql ��ѯ���
	 * @param clazz ��װ���ݵ�Javabean���class����
	 * @param params SQL�Ĳ���
	 * @return ��ѯ���Ľ��
	 */
	public List<?> queryRows(String sql, Class<?> clazz, Object[] params);
	
	/**
	 * ��ѯ����һ�м�¼�������ü�¼��װ��clazzָ������Ķ�����
	 * @param sql ��ѯ���
	 * @param clazz ��װ���ݵ�Javabean���class����
	 * @param params SQL�Ĳ���
	 * @return ��ѯ���Ľ��
	 */
	public Object queryUniqueRow(String sql, Class<?> clazz, Object[] params);
	
	/**
	 * ��ѯ����һ��ֵ��һ��һ�У���������ֵ����
	 * @param sql ��ѯ���
	 * @param params SQL�Ĳ���
	 * @return ��ѯ���Ľ��
	 */
	public Object queryValue(String sql, Object[] params);
	
	/**
	 * ��ѯ����һ�����֣�һ��һ�У���������ֵ����
	 * @param sql ��ѯ���
	 * @param params SQL�Ĳ���
	 * @return ��ѯ���Ľ��
	 */
	public Number queryNumber(String sql, Object[] params);

}

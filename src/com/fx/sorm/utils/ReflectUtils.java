package com.fx.sorm.utils;

import java.lang.reflect.Method;

/**
 * 封装了反射常用操作
 * @author Administrator
 *
 */
public class ReflectUtils {
	
	/**
	 * 调用obj对象对应的属性fieldName的get方法
	 * @param fieldName
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("all")
	public static Object invokeGet(String fieldName, Object obj) {
		try {
			Class c = obj.getClass();
			Method m = c.getMethod("get" + StringUtils.firstChar2UpperCass(fieldName), null);
			return m.invoke(obj, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 调用obj对象对应的属性fieldName的set方法
	 * @param fieldName
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("all")
	public static void invokeSet(String fieldName, Object value, Object obj) {
		try {
			if (null != value) {
				Class c = obj.getClass();
				Method m = c.getDeclaredMethod("set" + StringUtils.firstChar2UpperCass(fieldName), value.getClass());
				m.invoke(obj, value);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

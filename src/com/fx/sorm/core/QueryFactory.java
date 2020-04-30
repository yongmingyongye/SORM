package com.fx.sorm.core;

@SuppressWarnings("all")
public class QueryFactory {
	
	private static Query prototypeObj; // 原型对象
	
	static {
		try {
			// 加载指定的query类
			Class c = Class.forName(DBManager.getConf().getQueryClass());
			prototypeObj = (Query) c.getConstructor().newInstance();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private QueryFactory() {
	}
	
	private static class SingletonClassInstance {
		private static final QueryFactory factory = new QueryFactory();
	}
	
	public static QueryFactory getInstance() {
		return SingletonClassInstance.factory;
	}
	
	public Query createQuery() {
		try {
			return (Query) prototypeObj.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}

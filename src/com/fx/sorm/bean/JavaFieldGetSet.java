package com.fx.sorm.bean;

/**
 * ��װ��java���Ե�get��set������Դ��
 * 
 * @author Administrator
 *
 */
public class JavaFieldGetSet {

	/**
	 * ���Ե�Դ����Ϣ���磺private int userId
	 */
	private String fieldInfo;
	/**
	 * get������Դ����Ϣ��public int getUserId() {}
	 */
	private String getInfo;
	/**
	 * set������Դ����Ϣ��public void setUserId(int uesrId) {this.uesrId = userId;}
	 */
	private String setInfo;

	public JavaFieldGetSet() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JavaFieldGetSet(String fieldInfo, String getInfo, String setInfo) {
		super();
		this.fieldInfo = fieldInfo;
		this.getInfo = getInfo;
		this.setInfo = setInfo;
	}

	public String getFieldInfo() {
		return fieldInfo;
	}

	public void setFieldInfo(String fieldInfo) {
		this.fieldInfo = fieldInfo;
	}

	public String getGetInfo() {
		return getInfo;
	}

	public void setGetInfo(String getInfo) {
		this.getInfo = getInfo;
	}

	public String getSetInfo() {
		return setInfo;
	}

	public void setSetInfo(String setInfo) {
		this.setInfo = setInfo;
	}

	@Override
	public String toString() {
		System.out.println(fieldInfo);
		System.out.println(getInfo);
		System.out.println(setInfo);
		return super.toString();
	}
	
	

}

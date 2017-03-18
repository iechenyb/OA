package com.zzxy.oa.vo;

public class ContactsInfo {

	private int id; //ID
	private String userid;
	private String name; //姓名
	private String phonenum; //手机号
	private String officenum; //办公电话
	private String department; //部门
	private String py_name; //全拼
	private String py_szm_name; //首字母
	
	public ContactsInfo() {
		// TODO Auto-generated constructor stub
	}

	public ContactsInfo(int id, String userid, String name, String phonenum, String officenum,
			String department, String py_name, String py_szm_name) {
		super();
		this.id = id;
		this.userid = userid;
		this.name = name;
		this.phonenum = phonenum;
		this.officenum = officenum;
		this.department = department;
		this.py_name = py_name;
		this.py_szm_name = py_szm_name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhonenum() {
		return phonenum;
	}

	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}

	public String getOfficenum() {
		return officenum;
	}

	public void setOfficenum(String officenum) {
		this.officenum = officenum;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPy_name() {
		return py_name;
	}

	public void setPy_name(String py_name) {
		this.py_name = py_name;
	}

	public String getPy_szm_name() {
		return py_szm_name;
	}

	public void setPy_szm_name(String py_szm_name) {
		this.py_szm_name = py_szm_name;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	
	
}

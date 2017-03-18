package com.zzxy.oa.vo;

public class OnLineSearch {

	private String phoneNum; //手机号码
	private String departMent; //部门
	private String username; //姓名
	private String content; //考勤内容
	
	public OnLineSearch() {
		super();
	}
	
	public OnLineSearch(String phoneNum, String departMent, String username,
			String content) {
		super();
		this.phoneNum = phoneNum;
		this.departMent = departMent;
		this.username = username;
		this.content = content;
	}



	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getDepartMent() {
		return departMent;
	}
	public void setDepartMent(String departMent) {
		this.departMent = departMent;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}

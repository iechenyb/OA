package com.zzxy.oa.vo;

public class UserInfo {

	private String username; //�û���
	private String name; //��ʵ����
	private int loginstate; //��½״̬
	
	public UserInfo() {
		super();
	}
	
	public UserInfo(String username, String name, int loginstate) {
		super();
		this.username = username;
		this.name = name;
		this.loginstate = loginstate;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLoginstate() {
		return loginstate;
	}
	public void setLoginstate(int loginstate) {
		this.loginstate = loginstate;
	}
	
	
}

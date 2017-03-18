package com.zzxy.oa.vo;

public class DepartMent {

	private int id; //id
	private String name; //ÐÕÃû
	private String pinyin_name; //È«Æ´
	private String pinyin_szm_name; //Ê××ÖÄ¸
	
	public DepartMent() {
		super();
	}
	
	public DepartMent(int id, String name, String pinyin_name,
			String pinyin_szm_name) {
		super();
		this.id = id;
		this.name = name;
		this.pinyin_name = pinyin_name;
		this.pinyin_szm_name = pinyin_szm_name;
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
	public String getPinyin_name() {
		return pinyin_name;
	}
	public void setPinyin_name(String pinyin_name) {
		this.pinyin_name = pinyin_name;
	}
	public String getPinyin_szm_name() {
		return pinyin_szm_name;
	}
	public void setPinyin_szm_name(String pinyin_szm_name) {
		this.pinyin_szm_name = pinyin_szm_name;
	}
	
	
}

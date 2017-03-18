package com.zzxy.oa.vo;

public class KaoQin {

	private String id; //id
	private String weekdate; //ÈÕÆÚ
	private String content; //¿¼ÇÚÄÚÈİ
	
	public KaoQin() {
		super();
	}
	public KaoQin(String id, String weekdate, String content) {
		super();
		this.id = id;
		this.weekdate = weekdate;
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWeekdate() {
		return weekdate;
	}
	public void setWeekdate(String weekdate) {
		this.weekdate = weekdate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
	
}

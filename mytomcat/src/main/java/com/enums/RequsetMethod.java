package com.enums;

public enum RequsetMethod {
	
	GET(0,"get"),
	POST(1,"post");
	
	private Integer type;
	
	private String desc;
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	private RequsetMethod(Integer type, String desc) {
		this.type = type;
		this.desc = desc;
	}
	
}
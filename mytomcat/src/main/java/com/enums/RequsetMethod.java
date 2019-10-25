package com.enums;

public enum RequsetMethod {
	// HEAD,  PUT, PATCH, DELETE, OPTIONS, TRACE暂时不管了
	GET,
	POST;
	
	public static RequsetMethod getRequsetMethodByname(String name) {
		for(RequsetMethod method:RequsetMethod.values()) {
			if(method.name().equals(name)) {
				return method;
			}
		}
		return null;
	}
}

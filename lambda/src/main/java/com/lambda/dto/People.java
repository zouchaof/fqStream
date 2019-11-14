package com.lambda.dto;

import lombok.Data;

@Data
public class People {
	private String name;

	public People() {
	}
	
	public People(String name) {
		this.name = name;
	}
	
}

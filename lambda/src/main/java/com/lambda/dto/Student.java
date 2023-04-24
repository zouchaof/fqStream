package com.lambda.dto;

import lombok.Data;

@Data
public class Student {
	
	private Integer id;
	
	private String name;
	
	private Integer age;
	
	private String sex;
	
	private GoodStudent goodStudent;
}

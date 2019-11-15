package com.lambda.function;

import org.junit.Test;

import com.lambda.myinterface.MyLambdaModle;

public class MylamTest {
	
	@Test
	public void test1() {
		MyLambdaModle<Integer, String> lambdaModle = (x, y) -> {
			System.out.println(x);
			System.out.println(y);
		};
		lambdaModle.mytest(1, "string");
	}
	
	
}

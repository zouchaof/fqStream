package com.lambda.function;

import java.util.Comparator;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.junit.Test;

import com.lambda.dto.GoodStudent;
import com.lambda.dto.People;
import com.lambda.dto.Student;

public class FunctionTest {
	
	@Test
	public void test1() {
		Runnable runnable = () -> System.out.println("run");
		runnable.run();
	}
	
	/**
	 * ***fun***      方法					功能			参数		返回类型
	 * Predicate 	  boolean test(T t)		判断真假		T		boolean
	 * Consumer  	  void accept(T t) 		消费消息 		T 		void
	 * Function		  R apply(T t)			由T到R		T		R
	 * Supplier		  T get()				生产消息		none	T
	 * UnaryOperator  T apply(T t)			一元操作		T		T
	 * BinaryOperator T apply(T t, T t2)	二元操作		T, T	T
	 */
	@Test
	public void test2() {
		
		/** Predicate test **/
		Predicate<Integer> predicate = x -> x>1;
		boolean b = predicate.test(2);
		System.out.println("Predicate test return : " + b);

		/** Function test **/
		Consumer<String> consumer = x -> System.out.println(x);
		consumer.accept("consumer test");
		
		/** Function test **/
		Student student = new Student();
		student.setName("s1");
		GoodStudent goodStudent = new GoodStudent();
		goodStudent.setMark("good student mark");
		student.setGoodStudent(goodStudent);
		student.setAge(10);
		
		Function<Student, People> function = x -> {
			return new People(x.getName());
		};
		People p = function.apply(student);
		System.out.println(p.getName());
		
		Function<Student, GoodStudent> function2 = Student::getGoodStudent;
		GoodStudent r = function2.apply(student);
		System.out.println(r.getMark());
		
		/** Supplier test **/
		Supplier<Integer> supplier = () -> student.getAge();
		System.out.println(supplier.get());
		
		/** UnaryOperator test **/
		UnaryOperator<Integer> operator = i -> i+10;
		System.out.println(operator.apply(10));
		
		/** BinaryOperator test **/
		BinaryOperator<Integer> binaryOperator = (x, y) -> Math.max(x, y);
		System.out.println(binaryOperator.apply(10, 11));
	}
	
	
	
}

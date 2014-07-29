package org.pshow.controller;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BaseTest {
	
	private ClassPathXmlApplicationContext applicationContext;

	public BaseTest() {
		applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
	}
	
	protected <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

}

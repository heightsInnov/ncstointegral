package com.ubn.devops.ubnncsintegration.config;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
		log.error("**********************************************");
		log.error("Exception message - " + throwable.getMessage());
		log.error("Method name - " + method.getName());
		for (Object param : obj) {
			log.error("Parameter value - " + param);
		}
		log.error("**********************************************");
	}
}
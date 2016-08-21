package com.jasonfelege.todo.logging;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;

public class LogEventFactory {
	private final ApplicationContext context;
	
	public LogEventFactory(ApplicationContext context) {
		this.context = context;
	}

	public LogEvent getEvent(String name) {
		return context.getBean(LogEvent.class, name);
	}

	public LogEvent getEvent(String name, HttpServletRequest req) {
		LogEvent event = getEvent(name);
		
		if (event == null) return null;
		if (req == null) return event;
    	
    	return event;
	}
}

package com.jasonfelege.todo.exceptions;

public class MissingAuthenticationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public MissingAuthenticationException(String message) {
		super(message);
	}
	
}

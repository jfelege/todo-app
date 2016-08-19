package com.jasonfelege.todo.service;

public class JwtTokenValidationException extends Exception {

	private static final long serialVersionUID = 1L;

	public JwtTokenValidationException() {
		super();
	}
	
	public JwtTokenValidationException(String message) {
		super(message);
	}

	public JwtTokenValidationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public JwtTokenValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public JwtTokenValidationException(Throwable cause) {
		super(cause);
	}
	
}

package com.jasonfelege.todo.exceptions;

public class InvalidEntitlementException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public InvalidEntitlementException(String message) {
		super(message);
	}
}

package com.jasonfelege.todo.exceptions;

public	class NonExposingException extends Exception {
	private static final long serialVersionUID = 1L;

	public NonExposingException(String message) {
		super(message);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		// disable secure details from the stack_trace
		return this;
	}
}
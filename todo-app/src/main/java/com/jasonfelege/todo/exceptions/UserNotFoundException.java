package com.jasonfelege.todo.exceptions;

public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UserNotFoundException(String userName) {
		super("could not find user '" + userName + "'.");
	}
	
	public UserNotFoundException(long userId) {
		super("could not find user '" + userId + "'.");
	}
}

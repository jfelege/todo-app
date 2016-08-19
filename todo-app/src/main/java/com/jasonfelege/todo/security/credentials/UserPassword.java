package com.jasonfelege.todo.security.credentials;

import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;

public class UserPassword {
	private String value;

	public UserPassword(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		final String maskedValue = (value == null ? "null" : StringUtils.repeat('*', value.length()));
		
		return "UserPassword [value=" + maskedValue + "]";
	}
	
}

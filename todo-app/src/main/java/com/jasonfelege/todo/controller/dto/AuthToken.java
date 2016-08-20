package com.jasonfelege.todo.controller.domain;

public class AuthToken {
	private String type = "bearer";
	private String token;
	
	public AuthToken(String token) {
		this.token = token;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	@Override
	public String toString() {
		return "AuthToken [type=" + type + ", token=" + token + "]";
	}
}

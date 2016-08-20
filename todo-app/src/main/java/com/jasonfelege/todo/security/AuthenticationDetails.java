package com.jasonfelege.todo.security;

public class AuthenticationDetails {
	private String baseDomain;

	public String getBaseDomain() {
		return baseDomain;
	}

	public void setBaseDomain(String domain) {
		this.baseDomain = domain;
	}

	@Override
	public String toString() {
		return "AuthenticationDetails [baseDomain=" + baseDomain + "]";
	}
	
}

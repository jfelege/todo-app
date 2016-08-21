package com.jasonfelege.todo.security;

public class JsonWebToken {
	private String userName;
	private long userId;
	private String jwt;
	
	public JsonWebToken(String userName, long userId, String jwt) {
		this.userName = userName;
		this.userId = userId;
		this.jwt = jwt;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	@Override
	public String toString() {
		return "JwtClaims [userName=" + userName + ", userId=" + userId + ", jwt=" + jwt + "]";
	}
}

package com.jasonfelege.todo.service;

import java.util.Map;

public interface JsonWebTokenService {

	public enum ClaimTypes {
		userName,
		userId
		;
	};
	
	String generateToken(String userName, String userId);
	String generateToken(String userName, String userId, Map<String, Object> claims);
	
	Map<String, Object> verifyToken(String jwt) throws JwtTokenValidationException;
}

package com.jasonfelege.todo.service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;

public class JsonWebTokenServiceImpl implements JsonWebTokenService {
	private final JWTVerifier verifier;
	private final JWTSigner signer;
	
	private final String secret;
	private final String domain;
	private final long tokenExpiration;
	
	public JsonWebTokenServiceImpl(String secret, String domain, long tokenExpiration) {
		if (secret == null) {
			throw new NullPointerException("secret cannot be null");
		}
		
		if (domain == null) {
			throw new NullPointerException("domain cannot be null");
		}
		
		if (StringUtils.isEmpty(secret)) {
			throw new IllegalArgumentException("secret cannot be empty");
		}
		    
		if (StringUtils.isEmpty(domain)) {
			throw new IllegalArgumentException("domain cannot be empty");
		}
		
		this.secret = secret;
		this.domain = domain;
		this.tokenExpiration = tokenExpiration;
		
		signer = new JWTSigner(getSecret());
		verifier = new JWTVerifier(getSecret());
	}
	
	private long getTokenExpiration() {
		return this.tokenExpiration;
	}
	
	private String getDomain() {
		return this.domain;
	}
	
	private String getSecret() {
		return this.secret;
	}
	
	public Map<String, Object> verifyToken(String jwt) throws JwtTokenValidationException {

		try {
			 return verifier.verify(jwt);
		} catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException | SignatureException
				| IOException | JWTVerifyException e) {
			throw new JwtTokenValidationException(e);
		}

	}
	
	@Override
	public String generateToken(String userName, String userId) {	
		return generateToken(userName, userId, null);
	}


	@Override
	public String generateToken(String userName, String userId, Map<String, Object> claims) {

		final Map<String, Object> tokenClaims = new HashMap<String, Object>();
		
		if (claims != null) {
			tokenClaims.putAll(claims);
		}
		
		tokenClaims.put(ClaimTypes.userName.name(), userName);
		tokenClaims.put(ClaimTypes.userId.name(), userId);
		
		return generateToken(getSecret(), tokenClaims);
	}
	
	private String generateToken(String secret, Map<String, Object> claims) {
		final long iat = System.currentTimeMillis() / 1000l;
		final long exp = iat + getTokenExpiration();

		final HashMap<String, Object>  tokenClaims = new HashMap<String, Object>();
		
		if (claims != null) {
			tokenClaims.putAll(claims);
		}
		
		tokenClaims.put("iss", getDomain());
		tokenClaims.put("exp", exp);
		tokenClaims.put("iat", iat);
		
		return signer.sign(tokenClaims);
	}
	
}

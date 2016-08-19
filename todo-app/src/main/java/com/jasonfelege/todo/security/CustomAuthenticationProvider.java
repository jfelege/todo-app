package com.jasonfelege.todo.security;

import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.jasonfelege.todo.security.credentials.JsonWebToken;
import com.jasonfelege.todo.security.credentials.UserPassword;
import com.jasonfelege.todo.security.userdetails.CustomUserDetails;
import com.jasonfelege.todo.security.userdetails.CustomUserDetailsService;

public class CustomAuthenticationProvider implements AuthenticationProvider {
	private static final Logger LOG = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
	
	private CustomUserDetailsService userDetailsService;
	
	public CustomAuthenticationProvider(CustomUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {		
		LOG.info("action=generate_auth_token");
		
		String username = authentication.getName();
		
		if (authentication.isAuthenticated()) {
			LOG.info("action=generate_auth_token status=already_authorized user={}", username);
			return authentication;
		}
		
		CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
		
		if (user == null) {
			LOG.info("action=generate_auth_token status=user_not_found user={}", username);	
			throw new AuthenticationCredentialsNotFoundException("Username was not found");
		}
		
		Object credentials = authentication.getCredentials();
		
		if (credentials instanceof UserPassword) {
			String password = ((UserPassword)credentials).getValue();
			
			if (!BCrypt.checkpw(password, user.getPassword())) {
				LOG.info("action=generate_auth_token status=failed_password user={} password={}", username, password);		
				throw new AuthenticationCredentialsNotFoundException("Username or password was not accepted");
			}
		}
		else if (credentials instanceof JsonWebToken) {
			// this is a JWT token credential
		}
		else {
			// unknown credential type, fail out.
			LOG.info("action=generate_auth_token status=failed_password user={} password={}", username, credentials);		
			throw new AuthenticationCredentialsNotFoundException("Username or password was not accepted");
		}

		return new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword(), user.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	

}

package com.jasonfelege.todo.security;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

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
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();
		
		if (authentication.isAuthenticated()) {
			LOG.info("action=generate_auth_token status=already_authorized user={}", username);
			return authentication;
		}
		
		CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
		
		if (user == null) {
			LOG.info("action=generate_auth_token status=user_not_found user={}", username);	
			throw new AuthenticationCredentialsNotFoundException("Username was not found");
		}
		
		if (!BCrypt.checkpw(password, user.getPassword())) {
			LOG.info("action=generate_auth_token status=failed_password user={} password={}", username, password);		
			throw new AuthenticationCredentialsNotFoundException("Username or password was not accepted");
		}
		
		return new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword(), user.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	

}

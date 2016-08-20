package com.jasonfelege.todo.configuration.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

public class AuthenticationFilter extends GenericFilterBean {
	private final static Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);
	private final AuthenticationManager authenticationManager;

	public AuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
	
		HttpServletRequest httpRequest = (HttpServletRequest)request;

		final String uri = httpRequest.getRequestURI();
		
		LOG.info("action=authentication_filter uri={}", uri);
		
		String authHeader = httpRequest.getHeader("Authorization");
		
		String token = parseTokenFromHeader(authHeader);
		
		if (token != null) {
			processTokenAuthentication(token);
		}
	
		chain.doFilter(request, response);
	}
	
	private Authentication processTokenAuthentication(String token) {	
		PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(token,
				null);

		LOG.info("action=processTokenAuthentication msg=requesting_auth token={}", token);
		
		Authentication resultOfAuthentication = authenticationManager.authenticate(requestAuthentication);
		
		LOG.info("action=processTokenAuthentication result={}", resultOfAuthentication);
		
		if (resultOfAuthentication == null || !resultOfAuthentication.isAuthenticated()) {
			throw new InternalAuthenticationServiceException(
					"Unable to authenticate Domain User for provided credentials");
		}

		SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
		
		return resultOfAuthentication;
	}

	private String parseTokenFromHeader(String header) {
		// parse header: bearer <<token>>
		if (header == null)
			return null;
		if (header.isEmpty())
			return null;
		if (header.length() < 8)
			return null;
		if (!header.toLowerCase().startsWith("bearer "))
			return null;

		final String token = header.substring(7);

		return token;
	}
	
}

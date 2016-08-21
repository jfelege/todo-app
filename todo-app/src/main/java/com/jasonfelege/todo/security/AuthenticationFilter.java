package com.jasonfelege.todo.security;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

import com.jasonfelege.todo.logging.LogEvent;
import com.jasonfelege.todo.logging.LogEventFactory;

public class AuthenticationFilter extends GenericFilterBean {
	private final static Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);
	private final AuthenticationManager authenticationManager;
	private final LogEventFactory logEventFactory;

	public AuthenticationFilter(
			AuthenticationManager authenticationManager,
			LogEventFactory logEventFactory) {
		
		this.authenticationManager = authenticationManager;
		this.logEventFactory = logEventFactory;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		MDC.put("request_id", uuid);
		
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;

		final String uri = httpRequest.getRequestURI();
		
		final LogEvent event = logEventFactory.getEvent("authentication_filter", httpRequest);
		event.setHttpUri(uri);
		
		String authHeader = httpRequest.getHeader("Authorization");
		String token = parseTokenFromHeader(authHeader);
		event.addField("authorization", authHeader);

		try {
			processToken(event, httpRequest, token);
		}
		catch (BadCredentialsException e) {
			event.setStatus("bad_credential");
			event.setMessage(e.getMessage());
			event.addThrowableWithStacktrace(e);
			
			httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
			
			LOG.error(event.toString());
			return;
		}
		catch (MalformedURLException e) {
			event.setStatus("internal_error");
			event.setMessage(e.getMessage());
			
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			
			event.addThrowableWithStacktrace(e);
			LOG.error(event.toString());
			return;
		}
		
		event.setStatus("success");
		
		LOG.info(event.toString());
		
		chain.doFilter(request, response);
	}
	private void processToken(LogEvent event, HttpServletRequest httpRequest, String token) throws MalformedURLException {
		
		final String baseDomain = getURLBase(httpRequest);

		AuthenticationDetails details = new AuthenticationDetails();
		details.setBaseDomain(baseDomain);
		event.addField("baseDomain", details.getBaseDomain());
		
		final Authentication auth = processTokenAuthentication(token, details);
		final Object principal = auth.getPrincipal();
		
		if (principal instanceof JsonWebToken) {
			JsonWebToken jwtToken = (JsonWebToken)principal;
			
			event.setUser(jwtToken.getUserName());
			event.setUserId(String.valueOf(jwtToken.getUserId()));
			event.setToken(jwtToken.getJwt());
		}
	}
	private static String getURLBase(HttpServletRequest request) throws MalformedURLException {
	    URL requestURL = new URL(request.getRequestURL().toString());
	    String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
	    return requestURL.getProtocol() + "://" + requestURL.getHost() + port;
	}
	
	private Authentication processTokenAuthentication(String token, AuthenticationDetails details) {	
		PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(token,
				null);
		
		requestAuthentication.setDetails(details);

		Authentication resultOfAuthentication = authenticationManager.authenticate(requestAuthentication);
		
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

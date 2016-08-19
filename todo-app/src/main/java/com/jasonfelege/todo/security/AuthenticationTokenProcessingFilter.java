package com.jasonfelege.todo.security;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;
import com.jasonfelege.todo.security.credentials.JsonWebToken;
import com.jasonfelege.todo.security.userdetails.CustomUserDetails;
import com.jasonfelege.todo.security.userdetails.CustomUserDetailsService;
import com.jasonfelege.todo.service.AuthenticationService;
import com.jasonfelege.todo.service.JsonWebTokenService;
import com.jasonfelege.todo.service.JwtTokenValidationException;

public class AuthenticationTokenProcessingFilter extends GenericFilterBean {
	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationTokenProcessingFilter.class);
	private static final String JWT_CLAIM_USERNAME = JsonWebTokenService.ClaimTypes.userName.name();
	
	private AuthenticationManager authManager;
	private CustomUserDetailsService userDetailsService;
	private JsonWebTokenService jwtService;
	
	@Autowired
	private AuthenticationService authHelper;
	
	private final SecurityContextProvider securityContextProvider;
	private final WebAuthenticationDetailsSource webAuthenticationDetailsSource = new WebAuthenticationDetailsSource();

	public AuthenticationTokenProcessingFilter(
			JsonWebTokenService jwtService,
			AuthenticationManager authManager,
			CustomUserDetailsService userDetailsService,
			SecurityContextProvider securityContextProvider) {
		this.jwtService = jwtService;
		this.authManager = authManager;
		this.userDetailsService = userDetailsService;
		this.securityContextProvider = securityContextProvider;
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		if (request instanceof HttpServletRequest) {
			
			HttpServletRequest httpRequest = ((HttpServletRequest) request);
			
			LOG.debug("action=doFilter uri={}", httpRequest.getRequestURI());

			final String authHeader = httpRequest.getHeader("Authorization");

			if (!StringUtils.isEmpty(authHeader) && authHeader.toLowerCase().startsWith("bearer ")) {
				// strip leading string "bearer " from token
				final String token = authHeader.substring(7);

				try {
					final Map<String, Object> claims = jwtService.verifyToken(token);
					
					final String username = (String) claims.get(JWT_CLAIM_USERNAME);
					
					final CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

					final JsonWebToken claim = new JsonWebToken(userDetails.getName(), userDetails.getId(), token);
					
					//authenticateUser((HttpServletRequest) request,
					authHelper.authenticateUser((HttpServletRequest) request,
							userDetails.getName(), 
							claim,
							userDetails.getAuthorities());

					LOG.error("action=token_auth status=token_provisioned username={} token={}", userDetails.getName(), token);
				} catch (JwtTokenValidationException e) {
					((HttpServletResponse)response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					LOG.error("action=token_auth status=token_parse_exception error_message={}", e.getMessage());
					return;
				}
				
			}
		}
		else {
			LOG.warn("action=doFilter status=unknown_servlet_request");
		}
		
		chain.doFilter(request, response);
	}

	private void authenticateUser(HttpServletRequest request, String username, JsonWebToken claims,
			Collection<? extends GrantedAuthority> authorities) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, claims,
				authorities);

		WebAuthenticationDetails details = webAuthenticationDetailsSource.buildDetails(request);
		authentication.setDetails(details);

		SecurityContext sc = securityContextProvider.getSecurityContext();

		System.out.println("--- ---" + (authManager == null) + " " + authentication.getCredentials() + " "
				+ authentication.toString() + " " + details.toString());
		Authentication auth = authManager.authenticate(authentication);
		sc.setAuthentication(auth);
		System.out.println("--- authManager --- " + auth);
		
	}
}
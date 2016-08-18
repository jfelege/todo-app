package com.jasonfelege.todo.controller;

import java.util.Collections;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWTSigner;
import com.jasonfelege.todo.security.SecurityContextProvider;
import com.jasonfelege.todo.security.userdetails.CustomUserDetails;
import com.jasonfelege.todo.security.userdetails.CustomUserDetailsService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);
	
    private AuthenticationManager authManager;
	private CustomUserDetailsService userDetailsService;
    private final SecurityContextProvider securityContextProvider;
    private final WebAuthenticationDetailsSource webAuthenticationDetailsSource = new WebAuthenticationDetailsSource();
    
	
    public AuthenticationController(AuthenticationManager authManager, CustomUserDetailsService userDetailsService, SecurityContextProvider securityContextProvider) {
        this.securityContextProvider = securityContextProvider;
        this.userDetailsService = userDetailsService;
        this.authManager = authManager;
    }

	
	public String createToken(String userName, String userId) {
		final long iat = System.currentTimeMillis() / 1000l;
		final long exp = iat + 3600L;
		
		final JWTSigner signer = new JWTSigner("secret");
		final HashMap<String, Object>  claims = new HashMap<String, Object>();
		claims.put("iss", "http://localhost");
		claims.put("exp", exp);
		claims.put("iat", iat);
		claims.put("username", userName);
		claims.put("userId", userId);
		
		return signer.sign(claims);
	}
	
	@RequestMapping("/token")
	public String authenticate(String username, String password, HttpServletRequest request) {
		
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password, Collections.<GrantedAuthority>emptySet());
		WebAuthenticationDetails details = webAuthenticationDetailsSource.buildDetails(request);
		authentication.setDetails(details);
		
		SecurityContext sc = securityContextProvider.getSecurityContext();
		
		LOG.info("--- ---" + (authManager == null) + " " + authentication.getCredentials() + " " + authentication.toString() + " " + details.toString());
		
		Authentication auth;
		
		try {
			auth = authManager.authenticate(authentication);
		} catch (AuthenticationException e) {
			throw new AuthenticationServiceException("invalid username and/or password", e);
		}
		
		LOG.info("--- authManager --- " + auth);
		sc.setAuthentication(auth);
		
		CustomUserDetails userDetails = (CustomUserDetails)userDetailsService.loadUserByUsername(username);

		return createToken(userDetails.getName(), String.valueOf(userDetails.getId()));
	}
}

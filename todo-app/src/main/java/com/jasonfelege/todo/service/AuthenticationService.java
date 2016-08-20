package com.jasonfelege.todo.service;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.jasonfelege.todo.security.credentials.JsonWebToken;
import com.jasonfelege.todo.security.credentials.UserPassword;

public class AuthenticationService {

	private AuthenticationManager authManager;
    private final WebAuthenticationDetailsSource webAuthenticationDetailsSource = new WebAuthenticationDetailsSource();
    private final SecurityContext securityContext;
    
	public AuthenticationService(SecurityContext securityContext, AuthenticationManager authManager) {
		
		if (authManager == null) {
			throw new NullPointerException("authManager cannot be null");
		}
		
		if (securityContext == null) {
			throw new NullPointerException("securityContext cannot be null");
		}
	  
		this.securityContext = securityContext;
		this.authManager = authManager;
	}
    
	/*public AuthenticationService(SecurityContextProvider securityContextProvider, AuthenticationManager authManager) {
		
		if (authManager == null) {
			throw new NullPointerException("authManager cannot be null");
		}
		
		if (securityContextProvider == null) {
			throw new NullPointerException("securityContextProvider cannot be null");
		}
	  
		this.securityContext = null;
		this.securityContextProvider = securityContextProvider;
		this.authManager = authManager;
	}*/
	

	public Authentication authenticateUser(HttpServletRequest request, String username, JsonWebToken claims,
			Collection<? extends GrantedAuthority> authorities) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, claims,
				authorities);

		WebAuthenticationDetails details = webAuthenticationDetailsSource.buildDetails(request);
		authentication.setDetails(details);

		//SecurityContext sc = this.securityContextProvider.getSecurityContext();

		System.out.println("--- ---" + (authManager == null) + " " + authentication.getCredentials() + " "
				+ authentication.toString() + " " + details.toString());
		Authentication auth = authManager.authenticate(authentication);
		securityContext.setAuthentication(auth);
		System.out.println("--- authManager --- " + auth);
		return authentication;
	}

	
	public Authentication authenticateUser(HttpServletRequest request, String username, UserPassword claims,
			Collection<? extends GrantedAuthority> authorities) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, claims,
				authorities);
		
		WebAuthenticationDetails details = webAuthenticationDetailsSource.buildDetails(request);
		authentication.setDetails(details);

		System.out.println("--- ---" + (authManager == null) + " " + authentication.getCredentials() + " "
				+ authentication.toString() + " " + details.toString());
		Authentication auth = authManager.authenticate(authentication);
		securityContext.setAuthentication(auth);
		System.out.println("--- authManager --- " + auth);
		return authentication;
	}
}

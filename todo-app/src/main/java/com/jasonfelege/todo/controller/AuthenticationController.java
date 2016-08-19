package com.jasonfelege.todo.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jasonfelege.todo.controller.domain.AuthToken;
import com.jasonfelege.todo.security.SecurityContextProvider;
import com.jasonfelege.todo.security.credentials.UserPassword;
import com.jasonfelege.todo.security.userdetails.CustomUserDetails;
import com.jasonfelege.todo.security.userdetails.CustomUserDetailsService;
import com.jasonfelege.todo.service.AuthenticationService;
import com.jasonfelege.todo.service.JsonWebTokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);
	
    private final AuthenticationManager authManager;
	private final CustomUserDetailsService userDetailsService;
    private final SecurityContextProvider securityContextProvider;
    private final WebAuthenticationDetailsSource webAuthenticationDetailsSource = new WebAuthenticationDetailsSource();
    private final JsonWebTokenService jwtService;
    private final AuthenticationService authHelper;
	
    public AuthenticationController(AuthenticationService authHelper, AuthenticationManager authManager, CustomUserDetailsService userDetailsService, SecurityContextProvider securityContextProvider, JsonWebTokenService jwtService) {
        this.securityContextProvider = securityContextProvider;
        this.userDetailsService = userDetailsService;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.authHelper = authHelper;
    }
	
	@RequestMapping("/token")
	public AuthToken authenticate(String username, String password, HttpServletRequest request) {
		LOG.info("action=token_authentication username={} password={}", username, "[REDACTED]");
		UserPassword userPassword = new UserPassword(password);
		
		/*UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, userPassword);
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
		
		sc.setAuthentication(auth);
		
		LOG.info("--- authManager --- " + auth);
		*/
		
		final CustomUserDetails userDetails = (CustomUserDetails)userDetailsService.loadUserByUsername(username);

		//TODO this throws exception?
		authHelper.authenticateUser(request, username, userPassword, userDetails.getAuthorities());
		
		final String jwt = jwtService.generateToken(userDetails.getName(), String.valueOf(userDetails.getId()));

		return new AuthToken(jwt);
	}
}

package com.jasonfelege.todo.controller;

import javax.servlet.http.HttpServletRequest;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jasonfelege.todo.controller.dto.AuthToken;
import com.jasonfelege.todo.logging.LogEvent;
import com.jasonfelege.todo.logging.LogEventFactory;
import com.jasonfelege.todo.security.userdetails.CustomUserDetails;
import com.jasonfelege.todo.security.userdetails.CustomUserDetailsService;
import com.jasonfelege.todo.service.JsonWebTokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

	private final CustomUserDetailsService userDetailsService;
    private final JsonWebTokenService jwtService;
    private final LogEventFactory logEventFactory;
	
    public AuthenticationController(CustomUserDetailsService userDetailsService, 
    		JsonWebTokenService jwtService,
    		LogEventFactory logEventFactory) {
    	
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.logEventFactory = logEventFactory;
    }
	
	@RequestMapping(path = "/token",  method = { RequestMethod.GET, RequestMethod.POST })
	public AuthToken generate_auth_token(HttpServletRequest request, @RequestParam(required=true) String username ,@RequestParam(required=true) String password) {
		
		final LogEvent event = logEventFactory.getEvent("auth_token", request);
		event.setUser(username);
		event.setPassword("[REDACTED]");

		final CustomUserDetails userDetails = (CustomUserDetails)userDetailsService.loadUserByUsername(username);
		
		if (!BCrypt.checkpw(password, userDetails.getPassword())) {
			event.setStatus("failed_password_validation");
			throw new AuthenticationCredentialsNotFoundException("Username or password was not accepted");
		}
		
		final String jwt = jwtService.generateToken(userDetails.getUsername(), String.valueOf(userDetails.getId()));

		LOG.info(event.toString());
		
		return new AuthToken(jwt);
	}
}

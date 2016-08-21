package com.jasonfelege.todo.controller;

import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jasonfelege.todo.controller.dto.AuthToken;
import com.jasonfelege.todo.security.userdetails.CustomUserDetails;
import com.jasonfelege.todo.security.userdetails.CustomUserDetailsService;
import com.jasonfelege.todo.service.JsonWebTokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

	private final CustomUserDetailsService userDetailsService;
    private final JsonWebTokenService jwtService;
	
    public AuthenticationController(CustomUserDetailsService userDetailsService, JsonWebTokenService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }
	
	@RequestMapping(path = "/token",  method = { RequestMethod.GET, RequestMethod.POST })
	public AuthToken generate_auth_token(@RequestParam(required=true) String username ,@RequestParam(required=true) String password) {
		LOG.info("action=token_authentication username={} password={}", username, "[REDACTED]");

		final CustomUserDetails userDetails = (CustomUserDetails)userDetailsService.loadUserByUsername(username);
		
		if (!BCrypt.checkpw(password, userDetails.getPassword())) {
			LOG.info("action=generate_auth_token status=failed_password user={} password={}", username, password);		
			throw new AuthenticationCredentialsNotFoundException("Username or password was not accepted");
		}
		
		final String jwt = jwtService.generateToken(userDetails.getUsername(), String.valueOf(userDetails.getId()));

		return new AuthToken(jwt);
	}
}

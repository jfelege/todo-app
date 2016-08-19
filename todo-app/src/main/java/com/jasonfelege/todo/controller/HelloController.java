package com.jasonfelege.todo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloController {
	private static final Logger LOG = LoggerFactory.getLogger(HelloController.class);
	
	@RequestMapping("/")
	@Secured("ROLE_USER")
	public String helloWorld(Authentication auth, String token) {
		LOG.info("action=helloWorld authentication={} token={}", auth, token);
		
		String out = "hello ";
		
		if (auth != null) {
			out += auth.getName();
		}
		
		out += " "  + token;
		
		return out;
	}
	
	@RequestMapping("/admin")
	@Secured("ROLE_ADMIN")
	public String helloAdmin(Authentication auth, String token) {
		LOG.info("action=helloAdmin authentication={} token={}", auth, token);
		
		String out = "admin ";
		
		if (auth != null) {
			out += auth.getName();
		}
		
		out += " "  + token;
		
		return out;
	}
}

package com.jasonfelege.todo.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloController {

	@RequestMapping("/")
	@Secured("ROLE_USER")
	public String helloWorld(Authentication auth, String token) {
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
		String out = "admin ";
		
		if (auth != null) {
			out += auth.getName();
		}
		
		out += " "  + token;
		
		return out;
	}
}

package com.jasonfelege.todo.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextProvider {
	public SecurityContext getSecurityContext() {
		return SecurityContextHolder.getContext();
	}
}
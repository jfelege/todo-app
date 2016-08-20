package com.jasonfelege.todo.security.userdetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.jasonfelege.todo.data.UserRepository;
import com.jasonfelege.todo.data.domain.User;

public class CustomUserDetailsService implements UserDetailsService {
	private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	private UserRepository userRepo;
	
	public CustomUserDetailsService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepo.findOneByNameIgnoreCase(username);
		
		if (user == null) {
			LOG.info("action=loadUserByUsername username={} status=not_found", username);
			throw new UsernameNotFoundException("user " + username + " not found");
		}
		
		CustomUserDetails details = CustomUserDetails.fromUser(user);
		LOG.info("action=loadUserByUsername username={} status=found details={}", username, details);
		
		return details;
	}
	
	
}

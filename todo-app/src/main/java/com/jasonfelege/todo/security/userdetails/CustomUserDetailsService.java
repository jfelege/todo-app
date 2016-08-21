package com.jasonfelege.todo.security.userdetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.jasonfelege.todo.data.UserRepository;
import com.jasonfelege.todo.data.domain.User;
import com.jasonfelege.todo.exceptions.UserNotFoundException;
import com.jasonfelege.todo.logging.LogEvent;
import com.jasonfelege.todo.logging.LogEventFactory;

public class CustomUserDetailsService implements UserDetailsService {
	private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	private UserRepository userRepo;
	private final LogEventFactory logEventFactory;
	
	public CustomUserDetailsService(
			UserRepository userRepo,
			LogEventFactory logEventFactory) {
		this.userRepo = userRepo;
		this.logEventFactory = logEventFactory;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final LogEvent event = logEventFactory.getEvent("load_user_details");
		
		User user = userRepo.findOneByNameIgnoreCase(username)
				.orElseThrow(() -> new UserNotFoundException(username));
		
		CustomUserDetails details = CustomUserDetails.fromUser(user);
		
		event.setUser(username);
		event.addField("userDetails", details);
		
		LOG.info(event.toString());
		return details;
	}
}

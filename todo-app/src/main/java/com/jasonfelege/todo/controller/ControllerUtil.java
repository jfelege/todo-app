package com.jasonfelege.todo.controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;

import com.jasonfelege.todo.data.UserRepository;
import com.jasonfelege.todo.data.domain.User;
import com.jasonfelege.todo.exceptions.MissingAuthenticationException;
import com.jasonfelege.todo.exceptions.UserNotFoundException;

public class ControllerUtil {

	/**
	 * Method to verify the authentication object is not null
	 * @param auth - authentication object
	 * @return non-null authentication object
	 * @exception MissingAuthenticationException is thrown
	 */
	public static Authentication validateAuthentication(Authentication auth) {
		return Optional.ofNullable(auth).orElseThrow(
				() -> new MissingAuthenticationException("secured method received null authentication object"));
	}

	public static User validateUser(String name, UserRepository userRepository) {
		return userRepository.findOneByNameIgnoreCase(name).orElseThrow(() -> new UserNotFoundException(name));
	}
	
	public static User validateUser(long id, UserRepository userRepository) {
		return userRepository.findOneById(id).orElseThrow(() -> new UserNotFoundException(id));
	}
}

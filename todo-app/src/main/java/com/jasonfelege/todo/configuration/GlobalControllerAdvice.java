package com.jasonfelege.todo.configuration;

import java.io.IOException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jasonfelege.todo.exceptions.InvalidEntitlementException;
import com.jasonfelege.todo.exceptions.JwtTokenValidationException;
import com.jasonfelege.todo.exceptions.NonExposingException;
import com.jasonfelege.todo.exceptions.UserNotFoundException;

@ControllerAdvice
public class GlobalControllerAdvice {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerAdvice.class);
	
	private final boolean disableStackTrace;
	
	public GlobalControllerAdvice(@Value("${app.disableStackTrace}") Boolean hideStackTrace) {
		this.disableStackTrace = hideStackTrace;
	}
	
	@ExceptionHandler(InvalidEntitlementException.class)
	@ResponseBody
	public ResponseEntity<?> handleInvalidEntitlementException(HttpServletRequest req, Exception e) {
		return handleException(e, "unauthorized", HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseBody
	public ResponseEntity<?> handleEntityNotFoundException(HttpServletRequest req, Exception e) {
		return handleException(e, "resource not found", HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	@ResponseBody
	public ResponseEntity<?> handleUserNotFoundException(HttpServletRequest req, Exception e) {
		return handleException(e, "user not found", HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(AuthenticationException.class)
	@ResponseBody
	public ResponseEntity<?> handleAuthenticationException(HttpServletRequest req, Exception e) {
		return handleException(e, "unable to authenticate credentials", HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(JwtTokenValidationException.class)
	@ResponseBody
	public ResponseEntity<?> handleNotFoundException(HttpServletRequest req, Exception e) {
		return handleException(e, "unable to verify jwt token", HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(IOException.class)
	@ResponseBody
	public ResponseEntity<?> handleIOException(HttpServletRequest req, Exception e) {
		return handleException(e, "internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseBody
	public ResponseEntity<?> handleMissingServletRequestParameterException(HttpServletRequest req, Exception e) {
		return handleException(e, "missing request parameter", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	@ResponseBody
	public ResponseEntity<?> handleException(HttpServletRequest req, Exception e) {
		return handleException(e, "internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private ResponseEntity<?> handleException(Exception e, String message, HttpStatus status) {
		LOGGER.error(e.getMessage(), e);

		if (disableStackTrace) {
			e = (Exception) new NonExposingException(message);
		}

		return new ResponseEntity<>(e, status);
	}
}
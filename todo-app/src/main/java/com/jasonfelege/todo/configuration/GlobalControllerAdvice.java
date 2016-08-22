package com.jasonfelege.todo.configuration;

import java.io.IOException;
import java.util.NoSuchElementException;

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
import com.jasonfelege.todo.logging.LogEvent;
import com.jasonfelege.todo.logging.LogEventFactory;

@ControllerAdvice
public class GlobalControllerAdvice {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerAdvice.class);
	
	private final LogEventFactory logEventFactory;
	private final boolean disableStackTrace;
	
	public GlobalControllerAdvice(
			@Value("${app.disableStackTrace}") Boolean hideStackTrace,
			LogEventFactory logEventFactory) {
		this.disableStackTrace = hideStackTrace;
		this.logEventFactory = logEventFactory;
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
	
	@ExceptionHandler(NoSuchElementException.class)
	@ResponseBody
	public ResponseEntity<?> handleNoSuchElementException(HttpServletRequest req, Exception e) {
		return handleException(e, "not found", HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler
	@ResponseBody
	public ResponseEntity<?> handleException(HttpServletRequest req, Exception e) {
		return handleException(e, "internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private ResponseEntity<?> handleException(Exception e, String message, HttpStatus status) {
		final LogEvent event = logEventFactory.getEvent("error");
		event.setHttpStatus(String.valueOf(status.value()));
		event.setMessage(message);
		event.addThrowableWithStacktrace(e);
		
		if (disableStackTrace) {
			e = (Exception) new NonExposingException(message);
		}

		LOGGER.error(event.toString());
		return new ResponseEntity<>(e, status);
	}
}
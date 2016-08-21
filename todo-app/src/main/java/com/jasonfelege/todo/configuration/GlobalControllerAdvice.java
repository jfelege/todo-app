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
		LOGGER.error(e.getMessage(), e);

		if (disableStackTrace) {
			e = (Exception) new NonExposingException("unauthorized");
		}

		return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseBody
	public ResponseEntity<?> handleEntityNotFoundException(HttpServletRequest req, Exception e) {
		LOGGER.error(e.getMessage(), e);

		if (disableStackTrace) {
			e = (Exception) new NonExposingException("not found");
		}

		return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	@ResponseBody
	public ResponseEntity<?> handleUserNotFoundException(HttpServletRequest req, Exception e) {
		LOGGER.error(e.getMessage(), e);

		if (disableStackTrace) {
			e = (Exception) new NonExposingException("user not found");
		}

		return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(AuthenticationException.class)
	@ResponseBody
	public ResponseEntity<?> handleAuthenticationException(HttpServletRequest req, Exception e) {
		LOGGER.error(e.getMessage(), e);

		if (disableStackTrace) {
			e = (Exception) new NonExposingException("unable to authenticate credentials");
		}

		return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(JwtTokenValidationException.class)
	@ResponseBody
	public ResponseEntity<?> handleNotFoundException(HttpServletRequest req, Exception e) {
		LOGGER.error(e.getMessage(), e);

		if (disableStackTrace) {
			e = (Exception) new NonExposingException("unable to verify jwt token");
		}

		return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(IOException.class)
	@ResponseBody
	public ResponseEntity<?> handleIOException(HttpServletRequest req, Exception e) {
		LOGGER.error(e.getMessage(), e);

		if (disableStackTrace) {
			e = (Exception) new NonExposingException("internal server error");
		}

		return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler
	@ResponseBody
	public ResponseEntity<?> handleException(HttpServletRequest req, Exception e) {
		LOGGER.error(e.getMessage(), e);

		if (disableStackTrace) {
			e = (Exception) new NonExposingException("internal server error");
		}

		return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
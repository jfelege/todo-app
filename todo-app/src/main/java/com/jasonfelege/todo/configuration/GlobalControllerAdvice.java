package com.jasonfelege.todo.configuration;

import java.io.IOException;

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

import com.jasonfelege.todo.service.JwtTokenValidationException;

@ControllerAdvice
public class GlobalControllerAdvice {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerAdvice.class);

	class NonExposingException extends Exception {
		private static final long serialVersionUID = 1L;

		public NonExposingException(String message) {
			super(message);
		}

		@Override
		public synchronized Throwable fillInStackTrace() {
			// disable secure details from the stack_trace
			return this;
		}
	}

	private boolean disableStackTrace;
	
	public GlobalControllerAdvice(@Value("${app.disableStackTrace}") Boolean hideStackTrace) {
		this.disableStackTrace = hideStackTrace;
	}

	@ExceptionHandler(AuthenticationException.class)
	@ResponseBody
	public ResponseEntity<?> handleUserNotFoundException(HttpServletRequest req, Exception e) {
		LOGGER.error(e.getMessage(), e);

		if (disableStackTrace) {
			e = (Exception) new NonExposingException("invalid user credentials");
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
			e = (Exception) new NonExposingException("bad request");
		}

		return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
	}
}
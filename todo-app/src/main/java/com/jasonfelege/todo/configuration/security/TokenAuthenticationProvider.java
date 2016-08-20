package com.jasonfelege.todo.configuration.security;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.jasonfelege.todo.security.credentials.JsonWebToken;
import com.jasonfelege.todo.security.userdetails.CustomUserDetails;
import com.jasonfelege.todo.service.JsonWebTokenService;
import com.jasonfelege.todo.service.JwtTokenValidationException;

public class TokenAuthenticationProvider implements AuthenticationProvider {
	private final static Logger LOG = LoggerFactory.getLogger(TokenAuthenticationProvider.class);
	private static final String JWT_CLAIM_USERNAME = JsonWebTokenService.ClaimTypes.userName.name();

	private final JsonWebTokenService jwtService;
	private final UserDetailsService userDetailService;
	

	public TokenAuthenticationProvider(JsonWebTokenService jwtService, UserDetailsService userDetailService) {
		this.jwtService = jwtService;
		this.userDetailService = userDetailService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String jwt = (String) authentication.getPrincipal();
		
		if (jwt ==  null || jwt.isEmpty()) {
			throw new BadCredentialsException("Invalid token");
		}

		try {
			Map<String, Object> claims = jwtService.verifyToken(jwt);
			
			String username = (String)claims.get(JWT_CLAIM_USERNAME);
			
			CustomUserDetails userDetail = (CustomUserDetails)userDetailService.loadUserByUsername(username);
			
			if (userDetail == null) {
				// could not locate user from database
				return null;
			}
			
			LOG.info("action=authenticate_token username={} jwt={} userId={} authorities={}", username, jwt, userDetail.getId( ), userDetail.getAuthorities().size());
			
			
			JsonWebToken jwtToken = new JsonWebToken(userDetail.getUsername(), userDetail.getId(), jwt);
			
			PreAuthenticatedAuthenticationToken authenticatedToken = new PreAuthenticatedAuthenticationToken(
					jwtToken,
					null,
					userDetail.getAuthorities()
					);
			
			LOG.info("action=authenticate_token authentication={}", authenticatedToken);
			return authenticatedToken;
		}
		catch (JwtTokenValidationException e) {
			throw new BadCredentialsException("Invalid token or token expired", e);
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(PreAuthenticatedAuthenticationToken.class);
	}
}

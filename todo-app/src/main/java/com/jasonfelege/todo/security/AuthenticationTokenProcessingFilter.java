package com.jasonfelege.todo.security;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;
import com.jasonfelege.todo.security.userdetails.CustomUserDetails;
import com.jasonfelege.todo.security.userdetails.CustomUserDetailsService;

public class AuthenticationTokenProcessingFilter extends GenericFilterBean {
	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationTokenProcessingFilter.class);

	private AuthenticationManager authManager;
	private CustomUserDetailsService userDetailsService;
	private final SecurityContextProvider securityContextProvider;
	private final WebAuthenticationDetailsSource webAuthenticationDetailsSource = new WebAuthenticationDetailsSource();

	public AuthenticationTokenProcessingFilter(AuthenticationManager authManager,
			CustomUserDetailsService userDetailsService, SecurityContextProvider securityContextProvider) {
		this.userDetailsService = userDetailsService;
		this.securityContextProvider = securityContextProvider;
		this.authManager = authManager;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String token = null;

		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = ((HttpServletRequest) request);

			token = httpRequest.getHeader("Authorization");

			if (StringUtils.isEmpty(token)) {
				((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			
			if (!token.toLowerCase().startsWith("bearer ")) {
				((HttpServletResponse) response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
		}

		// strip leading string "bearer " from token
		token = token.substring(7);

		final String secret = "secret";

		// TODO create JWT service and abstract it out of here
		final JWTVerifier verifier = new JWTVerifier(secret);

		try {
			// jwt is valid, process the login request.

			final Map<String, Object> claims = verifier.verify(token);
			final String username = (String) claims.get("username");

			final CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

			authenticateUser((HttpServletRequest) request, userDetails.getName(), userDetails.getPassword(),
					userDetails.getAuthorities());

			LOG.error("action=token_auth status=token_provisioned username={} token={}", userDetails.getName(), token);
		} catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException | SignatureException
				| JWTVerifyException e) {
			LOG.error("action=token_auth status=token_parse_exception error_message={}", e.getMessage());
		}

		chain.doFilter(request, response);
	}

	private void authenticateUser(HttpServletRequest request, String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password,
				authorities);

		WebAuthenticationDetails details = webAuthenticationDetailsSource.buildDetails(request);
		authentication.setDetails(details);

		SecurityContext sc = securityContextProvider.getSecurityContext();

		System.out.println("--- ---" + (authManager == null) + " " + authentication.getCredentials() + " "
				+ authentication.toString() + " " + details.toString());
		Authentication auth = authManager.authenticate(authentication);
		System.out.println("--- authManager --- " + auth);
		sc.setAuthentication(auth);
	}
}
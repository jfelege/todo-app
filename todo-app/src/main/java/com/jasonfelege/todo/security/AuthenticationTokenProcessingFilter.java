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
import com.jasonfelege.todo.security.userdetails.CustomUserDetails;
import com.jasonfelege.todo.security.userdetails.CustomUserDetailsService;

public class AuthenticationTokenProcessingFilter extends GenericFilterBean {
	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationTokenProcessingFilter.class);
	
	private AuthenticationManager authManager;	
	private CustomUserDetailsService userDetailsService;
	private final SecurityContextProvider securityContextProvider;
	private final WebAuthenticationDetailsSource webAuthenticationDetailsSource = new WebAuthenticationDetailsSource();

	public AuthenticationTokenProcessingFilter(AuthenticationManager authManager, CustomUserDetailsService userDetailsService, SecurityContextProvider securityContextProvider) {
		this.userDetailsService = userDetailsService;
		this.securityContextProvider = securityContextProvider;
		this.authManager = authManager;
	}

	@Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
    	
        Map<String, String[]> parms = request.getParameterMap();

        if(parms.containsKey("token")) {
        	String token = parms.get("token")[0];
        	System.out.println(token);
        	
        	final String secret = "secret";

        	    final JWTVerifier verifier = new JWTVerifier(secret);
        	    
        	    try {
        	    	// if jwt is valid, authenticate the user as if they manually logged in.
        	    	
        	    	final Map<String,Object> claims= verifier.verify(token);
        	    	final String username = (String)claims.get("username");
        	    	
        	    	final CustomUserDetails userDetails = (CustomUserDetails)userDetailsService.loadUserByUsername(username);
        	    	
        	    	authenticateUser((HttpServletRequest)request, userDetails.getName(), userDetails.getPassword(), userDetails.getAuthorities());
					
				} catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException | SignatureException
						| JWTVerifyException e) {
					LOG.error("action=token_auth status=token_parse_exception error_message={}", e.getMessage());
				}
        	    
           // authenticateUser((HttpServletRequest)request, "jfelege", "password");
          
        }
        

        chain.doFilter(request, response);
    }

	private void authenticateUser(HttpServletRequest request, String username, String password, Collection<? extends GrantedAuthority> authorities) {
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
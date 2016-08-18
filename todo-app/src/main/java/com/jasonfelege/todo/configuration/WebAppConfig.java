package com.jasonfelege.todo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jasonfelege.todo.security.AuthenticationTokenProcessingFilter;
import com.jasonfelege.todo.security.CustomAuthenticationEntryPoint;
import com.jasonfelege.todo.security.CustomAuthenticationProvider;
import com.jasonfelege.todo.security.SecurityContextProvider;
import com.jasonfelege.todo.security.data.UserRepository;
import com.jasonfelege.todo.security.userdetails.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebAppConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private AuthenticationManager authManager;
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService(userRepo);
	}
	
	@Bean 
	public CustomUserDetailsService getCustomUserDetailsService() {
		return (CustomUserDetailsService)userDetailsService();
	}
	
	@Bean
	public CustomAuthenticationProvider getCustomAuthenticationProvider() {
		return new CustomAuthenticationProvider(getCustomUserDetailsService());
	}
	
	@Bean
	public AuthenticationTokenProcessingFilter getAuthenticationTokenProcessingFilter() {
		return new AuthenticationTokenProcessingFilter(authManager, getCustomUserDetailsService(), securityContextProvider());
	}

	@Autowired
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	  auth.authenticationProvider(getCustomAuthenticationProvider());
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		.authenticationProvider(getCustomAuthenticationProvider())
		.authorizeRequests()
			.antMatchers("/api/auth/token").permitAll()
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.addFilterBefore(getAuthenticationTokenProcessingFilter(),
						UsernamePasswordAuthenticationFilter.class);

	}

	@Bean
	public SecurityContextProvider securityContextProvider() {
		return new SecurityContextProvider();
	}

	@Bean
	public AuthenticationEntryPoint getCustomAuthenticationEntryPoint() {
		CustomAuthenticationEntryPoint point = new CustomAuthenticationEntryPoint();
		return point;
	}	
}
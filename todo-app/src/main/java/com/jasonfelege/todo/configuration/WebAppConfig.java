package com.jasonfelege.todo.configuration;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
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

import com.jasonfelege.todo.data.ChecklistRepository;
import com.jasonfelege.todo.data.ItemRepository;
import com.jasonfelege.todo.security.AuthenticationFilter;
import com.jasonfelege.todo.security.TokenAuthenticationProvider;
import com.jasonfelege.todo.security.data.UserRepository;
import com.jasonfelege.todo.security.userdetails.CustomUserDetailsService;
import com.jasonfelege.todo.service.ChecklistService;
import com.jasonfelege.todo.service.ChecklistServiceImpl;
import com.jasonfelege.todo.service.ItemService;
import com.jasonfelege.todo.service.ItemServiceImpl;
import com.jasonfelege.todo.service.JsonWebTokenService;
import com.jasonfelege.todo.service.JsonWebTokenServiceImpl;

@Configuration
@ComponentScan("com.jasonfelege.todo.service")
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebAppConfig extends WebSecurityConfigurerAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(WebAppConfig.class);
	
	@Value("${jwt.secret}")
	private String jwtTokenSecret;

	@Value("${app.domain}")
	private String appDomain;

	@Value("${jwt.expiration:300}")
	private long jwtExpiration;

	
	@Bean
	public JsonWebTokenService jsonWebTokenService() {
		LOG.info("getJsonWebTokenService {} {} {}", jwtTokenSecret, appDomain, jwtExpiration);
		
		return new JsonWebTokenServiceImpl(jwtTokenSecret, appDomain, jwtExpiration);
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
	    web
	      .ignoring()
	        .antMatchers("/api/auth/token");
	}
	
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.
		csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeRequests()
			.anyRequest().authenticated()
		.and()
			.anonymous().disable()
		.exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint());
		
		http
			.addFilterBefore(
					new AuthenticationFilter(authenticationManager()), 
				UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(tokenAuthenticationProvider());
	}
	
	@Bean
	public AuthenticationProvider tokenAuthenticationProvider() throws Exception {
		TokenAuthenticationProvider provider = new TokenAuthenticationProvider(
				jsonWebTokenService(),
				userDetailsService()
				);
		return provider;
	}

	@Bean
	public AuthenticationEntryPoint unauthorizedEntryPoint() {
		return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Autowired
	private UserRepository userRepo;
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService(userRepo);
	}

	@Bean
	public CustomUserDetailsService getCustomUserDetailsService() {
		return (CustomUserDetailsService) userDetailsService();
	}
	
	@Bean
	public ChecklistService getChecklistService(@Autowired ChecklistRepository checklistRepository) {
		return new ChecklistServiceImpl(checklistRepository);
	}
	
	@Bean
	public ItemService getItemService(@Autowired ItemRepository itemRepository) {
		return new ItemServiceImpl(itemRepository);
	}
	
}
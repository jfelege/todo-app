package com.jasonfelege.todo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.jasonfelege.todo.exceptions.JwtTokenValidationException;
import com.jasonfelege.todo.service.JsonWebTokenService;
import com.jasonfelege.todo.service.impl.JsonWebTokenServiceImpl;

@RunWith(SpringRunner.class)
public class JsonWebTokenServiceImplTest {

	private final String secret = "abc123";
	private final String domain = "http://localhost";
	private final long tokenExpiration = 6000l;

	private JsonWebTokenService jwtService = new JsonWebTokenServiceImpl(secret, domain, tokenExpiration);

	@Test
	public void verifyDomainClaim()  throws JwtTokenValidationException {
		String jwt = jwtService.generateToken("abc", "100");
		
		assertThat(jwt).isNotNull().isNotEmpty();
		
		Map<String, Object> claims = jwtService.verifyToken(jwt);

		assertThat(claims).isNotNull();
		
		assertThat(claims.get("iss")).isNotNull().isEqualTo(domain);
	}
	
	@Test
	public void verifyExpirationClaim()  throws JwtTokenValidationException {
		String jwt = jwtService.generateToken("abc", "100");
		
		assertThat(jwt).isNotNull().isNotEmpty();
		
		Map<String, Object> claims = jwtService.verifyToken(jwt);

		assertThat(claims).isNotNull();
		
		// these are really long-native types; not sure how to do OR with junit asserts
		assertThat(claims.get("iat")).isNotNull().isInstanceOf(Integer.class);
		assertThat(claims.get("exp")).isNotNull().isInstanceOf(Integer.class);
		
		long iat = Long.parseLong(claims.get("iat").toString());
		long exp = Long.parseLong(claims.get("exp").toString());
		
		assertThat((exp - iat)).isEqualTo(tokenExpiration);
	}
	
	@Test
	public void verifyBasicJwt() throws JwtTokenValidationException {
		String userName = "user_name";
		String userId = "100";

		String jwt = jwtService.generateToken(userName, userId);

		assertThat(jwt).isNotNull().isNotEmpty();

		Map<String, Object> claims = jwtService.verifyToken(jwt);

		assertThat(claims).isNotNull();

		assertThat(claims.get(JsonWebTokenService.ClaimTypes.userId.name())).isNotNull().isInstanceOf(String.class)
				.isEqualTo(userId);

		assertThat(claims.get(JsonWebTokenService.ClaimTypes.userName.name())).isNotNull().isInstanceOf(String.class)
				.isEqualTo(userName);
	}

	
	@Test
	public void verifyBasicJwtWithClaims() throws JwtTokenValidationException {
		String userName = "user_name";
		String userId = "100";

		final Map<String, Object> baseClaims = new HashMap<String, Object>();
		baseClaims.put("abc", "123");
		baseClaims.put("zyx", "098");
		
		String jwt = jwtService.generateToken(userName, userId, baseClaims);

		assertThat(jwt).isNotNull().isNotEmpty();

		Map<String, Object> claims = jwtService.verifyToken(jwt);

		assertThat(claims).isNotNull();

		assertThat(claims.get(JsonWebTokenService.ClaimTypes.userId.name())).isNotNull().isInstanceOf(String.class)
				.isEqualTo(userId);

		assertThat(claims.get(JsonWebTokenService.ClaimTypes.userName.name())).isNotNull().isInstanceOf(String.class)
				.isEqualTo(userName);
		
		assertThat(claims.get("abc")).isNotNull().isEqualTo("123");
		assertThat(claims.get("zyx")).isNotNull().isEqualTo("098");

	}
	
}

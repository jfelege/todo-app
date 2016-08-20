package com.jasonfelege.todo;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.jasonfelege.todo.service.JsonWebTokenService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplicationConfiguration.class)
@WebAppConfiguration
@ActiveProfiles("int-test")
public class HelloControllerTest {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/generated-snippets");
	
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private JsonWebTokenService jwtService;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation))
				.apply(springSecurity())
				.build();
	}
	
	@Test
	public void testGenerateAuthTokenSecurityWithAccount() throws Exception {
		this.mockMvc.perform(
				post("/api/auth/token")
					.param("username", "activeuser").param("password",  "password")
					.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is2xxSuccessful())
		.andExpect(jsonPath("$.type", is("bearer")))
		.andExpect(jsonPath("$.token").isNotEmpty())
		.andDo(document("token-successful",
				responseFields(
						fieldWithPath("type").description("type of token being returned"),
						fieldWithPath("token").description("actual token string to be used in subsequent calls")
						)
				));
	}
	
	
	@Test
	public void testGenerateAuthTokenWithInvalidCredentials() throws Exception {
		this.mockMvc.perform(
				post("/api/auth/token")
					.param("username", "activeuser").param("password",  "invalidpassword")
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
	}
	
	@Test
	public void testGenerateAuthTokenWithNonExistantCredentials() throws Exception {
		this.mockMvc.perform(
				post("/api/auth/token")
					.param("username", "nonexisting").param("password",  "password")
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
	}
	
	@Test
	public void testChecklistsWithMissingAuthToken() throws Exception {
		this.mockMvc.perform(get("/api/checklists").accept(MediaType.APPLICATION_JSON)) 
		.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
		.andDo(document("checklist-invalid-token"));
	}
	
	@Test
	public void testChecklistsWithAuthToken() throws Exception {
		String jwt = jwtService.generateToken("activeuser", "2");
		
		this.mockMvc.perform(
				get("/api/checklists")
				.header("Authorization", "bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON)) 
		.andExpect(status().is(HttpStatus.OK.value()))
		.andDo(document("checklist-succesful"));
	}
}

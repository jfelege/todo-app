package com.jasonfelege.todo;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonfelege.todo.controller.dto.ChecklistDto;
import com.jasonfelege.todo.service.JsonWebTokenService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplicationConfiguration.class)
@WebAppConfiguration
@ActiveProfiles("int-test")
public class IntegrationTest {
	private static final Logger LOG = LoggerFactory.getLogger(IntegrationTest.class);
	
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
		.andDo(document("checklist-list-succesful"));
	}
	
	@Test
	public void testCreateFetchChecklistsWithAuthToken() throws Exception {
		String jwt = jwtService.generateToken("activeuser", "2");
		
		MvcResult result = this.mockMvc.perform(
				post("/api/checklists")
				.content("{\"name\": \"test list\"}")
				.header("Authorization", "bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)) 
		.andExpect(status().is(HttpStatus.OK.value()))
		.andExpect(jsonPath("$.id").isNumber())
		.andExpect(jsonPath("$.name").isNotEmpty())
		.andExpect(jsonPath("$.self_href").isNotEmpty())
		.andReturn();
		
		ObjectMapper mapper = new ObjectMapper();
		ChecklistDto dto = mapper.readValue(result.getResponse().getContentAsString(), ChecklistDto.class);
		
		String uri = "/api/checklists/" + dto.getId();
		
		LOG.info("action=testCreateUpdateChecklistsWithAuthToken new_id={} uri={}", dto.getId(), uri);
		
		this.mockMvc.perform(
				get(uri)
				.header("Authorization", "bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON)) 
		.andExpect(status().is(HttpStatus.OK.value()))
		.andExpect(jsonPath("$.id").isNumber())
		.andExpect(jsonPath("$.name").isNotEmpty())
		.andExpect(jsonPath("$.self_href").isNotEmpty())
		.andDo(document("checklist-get-checklist-successful",responseFields(
				fieldWithPath("id").description("id of checklist requested"),
				fieldWithPath("name").description("name of the checklist"),
				fieldWithPath("self_href").description("hyperlink to resource")
				)));
	}
	
	@Test
	public void testCreatePutChecklistsWithAuthToken() throws Exception {
		String jwt = jwtService.generateToken("activeuser", "2");
		
		MvcResult result = this.mockMvc.perform(
				post("/api/checklists")
				.content("{\"name\": \"test list\"}")
				.header("Authorization", "bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)) 
		.andExpect(status().is(HttpStatus.OK.value()))
		.andExpect(jsonPath("$.id").isNumber())
		.andExpect(jsonPath("$.name").isNotEmpty())
		.andExpect(jsonPath("$.self_href").isNotEmpty())
		.andReturn();
		
		ObjectMapper mapper = new ObjectMapper();
		ChecklistDto dto = mapper.readValue(result.getResponse().getContentAsString(), ChecklistDto.class);
		
		String uri = "/api/checklists/" + dto.getId();
		
		LOG.info("action=testCreateUpdateChecklistsWithAuthToken new_id={} uri={}", dto.getId(), uri);
		
		this.mockMvc.perform(
				patch(uri)
				.content("{\"name\": \"test list!!\"}")
				.header("Authorization", "bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)) 
		.andExpect(status().is(HttpStatus.OK.value()))
		.andExpect(jsonPath("$.id").isNumber())
		.andExpect(jsonPath("$.name").isNotEmpty())
		.andExpect(jsonPath("$.name", is("test list!!")))
		.andExpect(jsonPath("$.self_href").isNotEmpty())
		.andDo(document("checklist-get-checklist-successful",responseFields(
				fieldWithPath("id").description("id of checklist requested"),
				fieldWithPath("name").description("name of the checklist"),
				fieldWithPath("self_href").description("hyperlink to resource")
				)));
	}
	
	@Test
	public void testCreateChecklistWithAuthToken() throws Exception {
		String jwt = jwtService.generateToken("activeuser", "2");
		
		this.mockMvc.perform(
				post("/api/checklists")
				.content("{\"name\": \"test list\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON)) 
		.andExpect(status().is(HttpStatus.OK.value()))
		.andExpect(jsonPath("$.id").isNotEmpty())
		.andExpect(jsonPath("$.name", is("test list")))
		.andExpect(jsonPath("$.self_href").isNotEmpty())
		.andDo(document("checklist-create-successful",responseFields(
				fieldWithPath("id").description("id of checklist created"),
				fieldWithPath("name").description("name of the checklist"),
				fieldWithPath("self_href").description("hyperlink to resource")
				)));
	}
	
	@Test
	public void testDeleteChecklistWithAuthToken() throws Exception {
		String jwt = jwtService.generateToken("activeuser", "2");
		
		this.mockMvc.perform(
				delete("/api/checklists/1")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON)) 
		.andExpect(status().is(HttpStatus.OK.value()))
		.andExpect(jsonPath("$.id").isNotEmpty())
		.andDo(document("checklist-delete-successful",responseFields(
				fieldWithPath("id").description("id of checklist deleted")
				)));
	}
	
	
	/*
	 * This test needs improvement of proper setup prior to deletion.
	 * @Test
	public void testDeleteNonOwnedChecklistWithAuthToken() throws Exception {
		String jwt = jwtService.generateToken("activeuser2", "3");

		testCreateChecklistWithAuthToken();
		
		this.mockMvc.perform(
				delete("/api/checklists/2")
				.header("Authorization", "bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON)) 
		.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
		.andDo(document("checklist-delete-nonowned-attempted"));
	}*/
	
	@Test
	public void testDeleteExistantChecklistWithAuthToken() throws Exception {
		String jwt = jwtService.generateToken("activeuser", "2");
		
		this.mockMvc.perform(
				delete("/api/checklists/1")
				.header("Authorization", "bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON)) 
		.andExpect(status().is(HttpStatus.NOT_FOUND.value()))
		.andDo(document("checklist-delete-nonexisting-successful"));
	}
	
	@Test
	public void testDeleteNonExistantChecklistWithAuthToken() throws Exception {
		String jwt = jwtService.generateToken("activeuser", "2");
		
		this.mockMvc.perform(
				delete("/api/checklists/3947402847")
				.header("Authorization", "bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON)) 
		.andExpect(status().is(HttpStatus.NOT_FOUND.value()))
		.andDo(document("checklist-delete-nonexistant"));
	}
	
	
}

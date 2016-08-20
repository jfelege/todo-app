package com.jasonfelege.todo.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jasonfelege.todo.controller.dto.ChecklistDto;
import com.jasonfelege.todo.data.UserRepository;
import com.jasonfelege.todo.data.domain.Checklist;
import com.jasonfelege.todo.data.domain.User;
import com.jasonfelege.todo.exceptions.JwtTokenValidationException;
import com.jasonfelege.todo.exceptions.UserNotFoundException;
import com.jasonfelege.todo.security.AuthenticationDetails;
import com.jasonfelege.todo.security.JsonWebToken;
import com.jasonfelege.todo.service.ChecklistService;

@RestController
@RequestMapping("/api/checklists")
public class ChecklistController {
	private static final Logger LOG = LoggerFactory.getLogger(ChecklistController.class);

	private final ChecklistService listService;
	private final UserRepository userRepository;

	public ChecklistController(ChecklistService listService, UserRepository userRepository) {

		this.listService = listService;
		this.userRepository = userRepository;
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = "/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ChecklistDto createChecklists(Authentication auth, @RequestBody ChecklistDto input)
			throws JwtTokenValidationException, JsonProcessingException {

		JsonWebToken token = (JsonWebToken) auth.getPrincipal();
		AuthenticationDetails authDetails = (AuthenticationDetails) auth.getDetails();
		final String baseDomain = authDetails.getBaseDomain();

		User owner = validateUser(token.getUserId());

		Checklist checklist = new Checklist();
		checklist.setName(input.getName());
		checklist.setOwner(owner);
		Checklist savedList = listService.save(checklist);

		ChecklistDto dto = ChecklistDto.fromEntity(savedList, baseDomain);
		return dto;

		/*
		 * Map<String, Object> claims =
		 * jwtService.verifyToken(authToken.substring(7));
		 * 
		 * String name =
		 * (String)claims.get(JsonWebTokenService.ClaimTypes.userName.name());
		 * long userId =
		 * Long.parseLong((String)claims.get(JsonWebTokenService.ClaimTypes.
		 * userId.name()));
		 * 
		 * LOG.info("action=helloWorld username={} userid={} token={}", name,
		 * userId, authToken);
		 * 
		 * User user = userRepository.findOne(userId);
		 * 
		 * Item item1 = new Item(); item1.setName("active item");
		 * item1.setComplete(false); Item item1b = itemService.save(item1);
		 * LOG.info("item_id={} item_name={} item_complete={}", item1b.getId(),
		 * item1b.getName(), item1b.isComplete());
		 * 
		 * Item item2 = new Item(); item2.setName("completed item");
		 * item2.setComplete(true); Item item2b = itemService.save(item2);
		 * LOG.info("item_id={} item_name={} item_complete={}", item2b.getId(),
		 * item2b.getName(), item2b.isComplete());
		 * 
		 * List<Item> items = new ArrayList<Item>(); items.add(item1b);
		 * items.add(item2b);
		 * 
		 * Checklist list1 = new Checklist(); list1.setOwner(user);
		 * list1.setName("My Todo List");
		 * 
		 * list1.setItems(items); Checklist newList = listService.save(list1);
		 * 
		 * return newList;
		 */

	}

	@Secured("ROLE_USER")
	@RequestMapping(method = RequestMethod.GET)
	public ChecklistIndex getChecklists(Authentication auth)
			throws JwtTokenValidationException, JsonProcessingException {

		LOG.info("action=get_checklists auth={} details={}", auth, auth.getDetails());

		JsonWebToken token = (JsonWebToken) auth.getPrincipal();
		AuthenticationDetails authDetails = (AuthenticationDetails) auth.getDetails();
		final String baseDomain = authDetails.getBaseDomain();

		validateUser(token.getUserId());

		List<ChecklistDto> checklists = new ArrayList<ChecklistDto>();

		List<Checklist> list = listService.findByOwnerId(token.getUserId()).orElse(Collections.emptyList());

		list.stream().forEach(item -> {
			ChecklistDto dto = ChecklistDto.fromEntity(item, baseDomain);
			checklists.add(dto);
		});

		LOG.info("action=get_checklists count={}", checklists.size());

		ChecklistIndex index = new ChecklistIndex();
		index.lists = checklists;

		return index;
	}

	private User validateUser(long userId) {
		return this.userRepository.findOneById(userId).orElseThrow(() -> new UserNotFoundException(userId));
	}

	class ChecklistIndex {
		public List<ChecklistDto> lists;
	}

}

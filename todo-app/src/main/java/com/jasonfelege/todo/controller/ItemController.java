package com.jasonfelege.todo.controller;


import static com.jasonfelege.todo.controller.ControllerUtil.validateAuthentication;
import static com.jasonfelege.todo.controller.ControllerUtil.validateUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jasonfelege.todo.controller.dto.ItemDto;
import com.jasonfelege.todo.data.ItemRepository;
import com.jasonfelege.todo.data.UserRepository;
import com.jasonfelege.todo.data.domain.Checklist;
import com.jasonfelege.todo.data.domain.Item;
import com.jasonfelege.todo.data.domain.User;
import com.jasonfelege.todo.exceptions.InvalidEntitlementException;
import com.jasonfelege.todo.exceptions.JwtTokenValidationException;
import com.jasonfelege.todo.security.AuthenticationDetails;
import com.jasonfelege.todo.security.JsonWebToken;
import com.jasonfelege.todo.service.ChecklistService;

@RestController
@RequestMapping("/api/items")
public class ItemController {
	private static final Logger LOG = LoggerFactory.getLogger(ItemController.class);

	private final ChecklistService listService;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	public ItemController(ChecklistService listService, ItemRepository itemRepository, UserRepository userRepository) {
		this.listService = listService;
		this.userRepository = userRepository;
		this.itemRepository = itemRepository;
	}
	
	@Secured("ROLE_USER")
	@RequestMapping(path="/{id}", method = RequestMethod.GET)
	public ItemDto getItem(Authentication auth, @PathVariable long id)
			throws JwtTokenValidationException, JsonProcessingException {

		auth = validateAuthentication(auth);
		
		LOG.info("action=get_item auth={} id={}", auth, id);

		JsonWebToken token = (JsonWebToken) auth.getPrincipal();
		AuthenticationDetails authDetails = (AuthenticationDetails) auth.getDetails();
		final String baseDomain = authDetails.getBaseDomain();

		User user = validateUser(token.getUserId(), userRepository);
		
		Item item = itemRepository.findOne(id);
		Checklist list = item.getChecklist();
		
		long userId = user.getId();
		long ownerId = list.getOwner().getId();
		
		if (ownerId != userId) {
			LOG.info("action=get_item id={} owner_id={} user_id={}", id, ownerId, userId);
			throw new InvalidEntitlementException("entity owned different user");
		}		

		ItemDto dto = ItemDto.fromEntity(item, baseDomain);
		
		return dto;
	}
}

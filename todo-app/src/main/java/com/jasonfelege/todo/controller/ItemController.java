package com.jasonfelege.todo.controller;

import static com.jasonfelege.todo.controller.ControllerUtil.validateAuthentication;
import static com.jasonfelege.todo.controller.ControllerUtil.validateUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jasonfelege.todo.controller.dto.ItemDto;
import com.jasonfelege.todo.data.UserRepository;
import com.jasonfelege.todo.data.domain.Checklist;
import com.jasonfelege.todo.data.domain.Item;
import com.jasonfelege.todo.data.domain.User;
import com.jasonfelege.todo.exceptions.InvalidEntitlementException;
import com.jasonfelege.todo.exceptions.JwtTokenValidationException;
import com.jasonfelege.todo.security.AuthenticationDetails;
import com.jasonfelege.todo.security.JsonWebToken;
import com.jasonfelege.todo.service.ChecklistService;
import com.jasonfelege.todo.service.ItemService;

@RestController
@RequestMapping("/api/items")
public class ItemController {
	private static final Logger LOG = LoggerFactory.getLogger(ItemController.class);

	private final ChecklistService listService;
	private final UserRepository userRepository;
	private final ItemService itemService;

	public ItemController(ChecklistService listService, ItemService itemService, UserRepository userRepository) {
		this.listService = listService;
		this.userRepository = userRepository;
		this.itemService = itemService;
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public ItemDto getItem(Authentication auth, @PathVariable long id)
			throws JwtTokenValidationException, JsonProcessingException {

		auth = validateAuthentication(auth);

		LOG.info("action=get_item auth={} id={}", auth, id);

		JsonWebToken token = (JsonWebToken) auth.getPrincipal();
		AuthenticationDetails authDetails = (AuthenticationDetails) auth.getDetails();
		final String baseDomain = authDetails.getBaseDomain();

		User user = validateUser(token.getUserId(), userRepository);

		Item item = itemService.findById(id);
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

	@Secured("ROLE_USER")
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
	public ItemDto putItem(Authentication auth, @PathVariable long id, @RequestBody ItemDto dto)
			throws JwtTokenValidationException, JsonProcessingException {

		auth = validateAuthentication(auth);

		LOG.info("action=update_item auth={} id={}", auth, id);

		JsonWebToken token = (JsonWebToken) auth.getPrincipal();
		AuthenticationDetails authDetails = (AuthenticationDetails) auth.getDetails();
		final String baseDomain = authDetails.getBaseDomain();

		User user = validateUser(token.getUserId(), userRepository);

		Item item = itemService.findById(id);
		
		Checklist list = listService.findById(item.getChecklist().getId()).get();

		long userId = user.getId();
		long ownerId = list.getOwner().getId();

		LOG.info("action=update_item itemId={} checklistId={} id={} owner_id={} user_id={} dto={}", 
				item.getId(), list.getId(), id, ownerId, userId, dto.getName());

		if (ownerId != userId) {
			LOG.info("action=delete_item id={} owner_id={} user_id={}", id, ownerId, userId);
			throw new InvalidEntitlementException("entity owned different user");
		}

		item.setName(dto.getName());
		item.setComplete(dto.isComplete());

		Item savedItem = itemService.save(item);

		ItemDto resultDto = ItemDto.fromEntity(savedItem, baseDomain);

		return resultDto;
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	public ItemDeleted deleteItem(Authentication auth, @PathVariable long id)
			throws JwtTokenValidationException, JsonProcessingException {

		auth = validateAuthentication(auth);

		LOG.info("action=delete_item auth={} id={}", auth, id);

		JsonWebToken token = (JsonWebToken) auth.getPrincipal();

		User user = validateUser(token.getUserId(), userRepository);

		Item item = itemService.findById(id);

		LOG.info("action=delete_item id={} itemId={} checklist_null={}", id, item.getId(),
				(item.getChecklist() == null ? true : false));

		Checklist list = listService.findById(item.getChecklist().getId()).get();

		long userId = user.getId();
		long ownerId = list.getOwner().getId();

		LOG.info("action=delete_item itemId={} checklistId={} id={} owner_id={} user_id={}", item.getId(), list.getId(),
				id, ownerId, userId);

		if (ownerId != userId) {
			LOG.info("action=delete_item id={} owner_id={} user_id={}", id, ownerId, userId);
			throw new InvalidEntitlementException("entity owned different user");
		}

		long deletedId = itemService.deleteById(item.getId());

		ItemDeleted dto = new ItemDeleted();
		dto.id = deletedId;

		return dto;
	}

	class ItemDeleted {
		public long id;
	}
}

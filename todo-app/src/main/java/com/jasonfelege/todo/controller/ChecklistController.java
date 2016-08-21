package com.jasonfelege.todo.controller;

import static com.jasonfelege.todo.controller.ControllerUtil.validateAuthentication;
import static com.jasonfelege.todo.controller.ControllerUtil.validateUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jasonfelege.todo.controller.dto.ChecklistDto;
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
@RequestMapping("/api/checklists")
public class ChecklistController {
	private static final Logger LOG = LoggerFactory.getLogger(ChecklistController.class);

	private final ChecklistService listService;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	public ChecklistController(ChecklistService listService, ItemRepository itemRepository, UserRepository userRepository) {
		this.listService = listService;
		this.userRepository = userRepository;
		this.itemRepository = itemRepository;
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	public ChecklistDeleted deleteChecklist(Authentication auth, @PathVariable long id)
			throws JwtTokenValidationException, JsonProcessingException {
		
		auth = validateAuthentication(auth);
		
		LOG.info("action=delete_checklist id={}", id);
		
		JsonWebToken token = (JsonWebToken) auth.getPrincipal();

		User user = validateUser(token.getUserId(), userRepository);
		
		Checklist list = listService.findById(id).orElseThrow(() -> new EntityNotFoundException("entity not found"));

		long userId = user.getId();
		long ownerId = list.getOwner().getId();
		
		if (ownerId != userId) {
			LOG.info("action=delete_checklist id={} owner_id={} user_id={}", id, ownerId, userId);
			throw new InvalidEntitlementException("entity owned different user");
		}
		
		Long savedList = Long.valueOf(listService.deleteById(id).get());
		
		ChecklistDeleted dto = new ChecklistDeleted();
		dto.id = savedList;
		return dto;
	}
	
	@Secured("ROLE_USER")
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ChecklistDto createChecklist(Authentication auth, @RequestBody ChecklistDto input)
			throws JwtTokenValidationException, JsonProcessingException {
		
		auth = validateAuthentication(auth);
		
		LOG.info("action=post_checklist auth={} dto={}", auth, input);
		
		JsonWebToken token = (JsonWebToken) auth.getPrincipal();
		AuthenticationDetails authDetails = (AuthenticationDetails) auth.getDetails();
		final String baseDomain = authDetails.getBaseDomain();

		User owner = validateUser(token.getUserId(), userRepository);

		Checklist checklist = new Checklist();
		checklist.setName(input.getName());
		checklist.setOwner(owner);
		

		
		List<Item> items = new ArrayList<Item>();
		checklist.setItems(items);
		
		Checklist savedList = listService.save(checklist);
		
		Item item1 = new Item();
		item1.setChecklist(savedList);
		item1.setComplete(false);
		item1.setName("item1-name");
		
		savedList = listService.save(checklist);

		ChecklistDto dto = ChecklistDto.fromEntity(savedList, baseDomain);
		return dto;
	}
	
	
	@Secured("ROLE_USER")
	@RequestMapping(path = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ChecklistDto updateChecklist(Authentication auth, @PathVariable long id, @RequestBody ChecklistDto input)
			throws JwtTokenValidationException, JsonProcessingException {
		
		auth = validateAuthentication(auth);
		
		LOG.info("action=put_checklist auth={} id={} dto={}", auth, id, input);
		
		JsonWebToken token = (JsonWebToken) auth.getPrincipal();
		AuthenticationDetails authDetails = (AuthenticationDetails) auth.getDetails();
		final String baseDomain = authDetails.getBaseDomain();

		validateUser(token.getUserId(), userRepository);

		Checklist checklist = listService.findById(id).get();
		checklist.setName(input.getName());
		
		Checklist savedList = listService.save(checklist);

		ChecklistDto dto = ChecklistDto.fromEntity(savedList, baseDomain);
		return dto;
	}

	@Secured("ROLE_USER")
	@RequestMapping(method = RequestMethod.GET)
	public ChecklistIndex getChecklists(Authentication auth)
			throws JwtTokenValidationException, JsonProcessingException {

		auth = validateAuthentication(auth);
		
		LOG.info("action=get_checklists auth={}", auth);

		JsonWebToken token = (JsonWebToken) auth.getPrincipal();
		AuthenticationDetails authDetails = (AuthenticationDetails) auth.getDetails();
		final String baseDomain = authDetails.getBaseDomain();

		validateUser(token.getUserId(), userRepository);

		List<ChecklistDto> checklists = new ArrayList<ChecklistDto>();

		List<Checklist> list = listService.findByOwnerId(token.getUserId()).orElse(Collections.emptyList());

		list.stream().forEach(item -> {
			ChecklistDto dto = ChecklistDto.fromEntity(item, baseDomain);
			checklists.add(dto);
		});

		ChecklistIndex index = new ChecklistIndex();
		index.lists = checklists;

		return index;
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public ChecklistDto getChecklist(Authentication auth, @PathVariable long id)
			throws JwtTokenValidationException, JsonProcessingException {

		auth = validateAuthentication(auth);
		
		LOG.info("action=put_checklist auth={} id={} dto={}", auth, id);

		JsonWebToken token = (JsonWebToken) auth.getPrincipal();
		AuthenticationDetails authDetails = (AuthenticationDetails) auth.getDetails();
		final String baseDomain = authDetails.getBaseDomain();

		User user = validateUser(token.getUserId(), userRepository);

		Checklist item = listService.findById(id).get();

		long userId = user.getId();
		long ownerId = item.getOwner().getId();
		
		if (ownerId != userId) {
			LOG.info("action=delete_checklist id={} owner_id={} user_id={}", id, ownerId, userId);
			throw new InvalidEntitlementException("entity owned different user");
		}

		ChecklistDto dto = ChecklistDto.fromEntity(item, baseDomain);

		return dto;
	}
	
	
	@Secured("ROLE_USER")
	@RequestMapping(path = "/{id}/items", method = RequestMethod.GET)
	public ChecklistItemIndex getChecklistItem(Authentication auth, @PathVariable long id)
			throws JwtTokenValidationException, JsonProcessingException {

		auth = validateAuthentication(auth);
		
		LOG.info("action=get_checklist_items auth={} id={} dto={}", auth, id);

		JsonWebToken token = (JsonWebToken) auth.getPrincipal();
		AuthenticationDetails authDetails = (AuthenticationDetails) auth.getDetails();
		final String baseDomain = authDetails.getBaseDomain();

		User user = validateUser(token.getUserId(), userRepository);

		Checklist list = listService.findById(id).get();
		
		long userId = user.getId();
		long ownerId = list.getOwner().getId();
		
		if (ownerId != userId) {
			LOG.info("action=delete_checklist id={} owner_id={} user_id={}", id, ownerId, userId);
			throw new InvalidEntitlementException("entity owned different user");
		}
		
		List<ItemDto> dtos = new ArrayList<ItemDto>();
		
		itemRepository.findByChecklistId(id).forEach(item -> {
			ItemDto dto = ItemDto.fromEntity(item, baseDomain);
			dtos.add(dto);
		});
		
		ChecklistItemIndex index = new ChecklistItemIndex();
		index.items = dtos;
		
		return index;
	}
	
	@Secured("ROLE_USER")
	@RequestMapping(path = "/{id}/items/{itemId}", method = RequestMethod.GET)
	public void createChecklistItem(Authentication auth, @PathVariable long id, @PathVariable long itemId, HttpServletResponse httpServletResponse)
			throws JwtTokenValidationException, IOException {
	
		auth = validateAuthentication(auth);
		
		LOG.info("action=create_checklist_item auth={} id={} dto={}", auth, id);

		JsonWebToken token = (JsonWebToken) auth.getPrincipal();
		AuthenticationDetails authDetails = (AuthenticationDetails) auth.getDetails();
		final String baseDomain = authDetails.getBaseDomain();
		
		ItemDto dto = new ItemDto();
		dto.setBaseDomain(baseDomain);
		dto.setId(id);
		dto.getSelfHref();
		
		httpServletResponse
			.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		httpServletResponse
			.setHeader("Location", dto.getSelfHref());
		httpServletResponse.setHeader("Connection", "close");
	}
	
	@Secured("ROLE_USER")
	@RequestMapping(path = "/{id}/items", method = RequestMethod.POST)
	public ItemDto createChecklistItem(Authentication auth, @PathVariable long id, @RequestBody ItemDto dto)
			throws JwtTokenValidationException, JsonProcessingException {

		auth = validateAuthentication(auth);
		
		LOG.info("action=create_checklist_item auth={} id={} dto={}", auth, id);

		JsonWebToken token = (JsonWebToken) auth.getPrincipal();
		AuthenticationDetails authDetails = (AuthenticationDetails) auth.getDetails();
		final String baseDomain = authDetails.getBaseDomain();

		User user = validateUser(token.getUserId(), userRepository);

		Checklist list = listService.findById(id).get();
		
		long userId = user.getId();
		long ownerId = list.getOwner().getId();
		
		if (ownerId != userId) {
			LOG.info("action=delete_checklist id={} owner_id={} user_id={}", id, ownerId, userId);
			throw new InvalidEntitlementException("entity owned different user");
		}
		
		Item itemEntity = new Item();
		itemEntity.setName(dto.getName());
		itemEntity.setComplete(dto.isComplete());
		itemEntity.setChecklist(list);
		
		Item savedItem = itemRepository.save(itemEntity);
		
		ItemDto resultDto = ItemDto.fromEntity(savedItem, baseDomain);
		
		return resultDto;
	}
	
	class ChecklistItemIndex {
		public List<ItemDto> items;
	}
	
	class ChecklistIndex {
		public List<ChecklistDto> lists;
	}
	
	class ChecklistDeleted {
		public long id;
	}

}

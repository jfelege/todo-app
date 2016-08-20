package com.jasonfelege.todo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jasonfelege.todo.data.domain.Checklist;
import com.jasonfelege.todo.data.domain.Item;
import com.jasonfelege.todo.security.data.User;
import com.jasonfelege.todo.security.data.UserRepository;
import com.jasonfelege.todo.service.ChecklistService;
import com.jasonfelege.todo.service.ItemService;
import com.jasonfelege.todo.service.JsonWebTokenService;
import com.jasonfelege.todo.service.JwtTokenValidationException;

@RestController
@RequestMapping("/api/checklists")
public class ChecklistController {
	private static final Logger LOG = LoggerFactory.getLogger(ChecklistController.class);
	
	private final ChecklistService listService;
	private final ItemService itemService;
	private final JsonWebTokenService jwtService;
	private final UserRepository userRepository;
	
	public ChecklistController(ChecklistService listService, 
			ItemService itemService, 
			JsonWebTokenService jwtService,
			UserRepository userRepository
			) {
		this.listService = listService;
		this.itemService = itemService;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}
	
	@Secured("ROLE_USER") 
	@RequestMapping(path = "/", method = RequestMethod.POST)
	public String createChecklists(@RequestHeader(value="Authorization") String authToken) throws JwtTokenValidationException, JsonProcessingException {
	    
		Map<String, Object> claims = jwtService.verifyToken(authToken.substring(7));

		String name = (String)claims.get(JsonWebTokenService.ClaimTypes.userName.name());
	    long userId = Long.parseLong((String)claims.get(JsonWebTokenService.ClaimTypes.userId.name()));
	    
		LOG.info("action=helloWorld username={} userid={} token={}", name, userId, authToken);
		
		User user = userRepository.findOne(userId);
		
		Item item1 = new Item();
		item1.setName("active item");
		item1.setComplete(false);
		Item item1b = itemService.save(item1);
		LOG.info("item_id={} item_name={} item_complete={}", item1b.getId(), item1b.getName(), item1b.isComplete());
		
		Item item2 = new Item();
		item2.setName("completed item");
		item2.setComplete(true);
		Item item2b = itemService.save(item2);
		LOG.info("item_id={} item_name={} item_complete={}", item2b.getId(), item2b.getName(), item2b.isComplete());
		
		List<Item> items = new ArrayList<Item>();
		items.add(item1b);
		items.add(item2b);
	
		Checklist list1 = new Checklist();
		list1.setOwner(user);
		list1.setName("My Todo List");
		
		Checklist newList = listService.save(list1);
		newList.setItems(items);
		listService.save(newList);
					
		return "saved";
	}
	
	@Secured("ROLE_USER")
	@RequestMapping(path = "/", method = RequestMethod.GET)
	public String getChecklists(@RequestHeader(value="Authorization") String authToken) throws JwtTokenValidationException, JsonProcessingException {
		return "hi";
	}
}

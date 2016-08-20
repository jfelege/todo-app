package com.jasonfelege.todo.service;

import java.util.List;

import com.jasonfelege.todo.data.domain.Item;

public interface ItemService {
	
	List<Item> findByChecklistId(long checklistId);
	
	Item save(Item item);
	
}

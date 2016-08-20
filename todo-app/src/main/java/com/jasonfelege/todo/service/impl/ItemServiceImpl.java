package com.jasonfelege.todo.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import com.jasonfelege.todo.data.ItemRepository;
import com.jasonfelege.todo.data.domain.Item;
import com.jasonfelege.todo.service.ItemService;

public class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepo;
	
	public ItemServiceImpl(ItemRepository itemRepo) {
		this.itemRepo = itemRepo;
	}
	
	public List<Item> findByChecklistId(long checklistId) {
		return itemRepo.findByChecklistId(checklistId);
	}
	
	@Transactional
	public Item save(Item item) {
		return itemRepo.save(item);
	}
}

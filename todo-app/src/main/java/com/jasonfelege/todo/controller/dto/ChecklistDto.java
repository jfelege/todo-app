package com.jasonfelege.todo.controller.domain;

import java.util.List;

public class ChecklistDTO {
	private long id;
	private String name;
	private OwnerDTO owner;
	private List<ItemDTO> items;
	
	public ChecklistDTO(long id, String name, OwnerDTO owner, List<ItemDTO> items) {
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.items = items;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<ItemDTO> getItems() {
		return items;
	}

	public OwnerDTO getOwner() {
		return owner;
	}
	
}

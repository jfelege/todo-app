package com.jasonfelege.todo.controller.domain;

public class OwnerDTO {
	private long id;
	private String name;
	
	public OwnerDTO(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}

package com.jasonfelege.todo.controller.domain;

public class ItemDTO {
	private long id;
	private String name;
	private boolean complete;
	
	public ItemDTO(long id, String name, boolean complete) {
		this.id = id;
		this.name = name;
		this.complete = complete;
	}
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public boolean isComplete() {
		return complete;
	}
	
	
}

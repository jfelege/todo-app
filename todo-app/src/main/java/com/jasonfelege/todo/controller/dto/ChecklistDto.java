package com.jasonfelege.todo.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jasonfelege.todo.data.domain.Checklist;

public class ChecklistDto {
	private static final String DTO_BASE_PATH = "/api/checklist";
	
	private long id;
	private String name;
	private String baseDomain;
	
	public ChecklistDto() {
		/* stub */
	}
	
	public ChecklistDto(String baseDomain) {
		setBaseDomain(baseDomain);
	}
	
	public void setBaseDomain(String baseDomain) {
		this.baseDomain = baseDomain;
	}
	
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonProperty("self_href")
	public String getSelfHref() {
		if (baseDomain == null)
			throw new IllegalStateException("baseDomain was never set");
		return baseDomain + DTO_BASE_PATH + "/" + id;
	}
	
	public static ChecklistDto fromEntity(Checklist list, String baseDomain) {
		ChecklistDto dto = new ChecklistDto(baseDomain);
		dto.setId(list.getId());
		dto.setName(list.getName());
		return dto;
	}
}

package com.jasonfelege.todo.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jasonfelege.todo.data.domain.Item;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDto {
	private static final String DTO_BASE_PATH = "/api/items";
	private static final String DTO_CHECKLIST_BASE_PATH = "/api/checklists";
	
	private long id;
	private String name;
	private boolean complete;
	private String baseDomain;
	
	private long checklistId;
	
	public ItemDto() {
		/* stub */
	}
	
	public ItemDto(String baseDomain) {
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
	
	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	@JsonProperty("self_href")
	public String getSelfHref() {
		if (baseDomain == null)
			throw new IllegalStateException("baseDomain was never set");
		return baseDomain + DTO_BASE_PATH + "/" + id;
	}
	
	public void setChecklistId(long checklistId) {
		this.checklistId = checklistId;
	}
	
	@JsonProperty("checklist_href")
	public String getChecklistHref() {
		if (baseDomain == null)
			throw new IllegalStateException("baseDomain was never set");
		return baseDomain + DTO_CHECKLIST_BASE_PATH + "/" + checklistId;
	}
	
	public static ItemDto fromEntity(Item item, String baseDomain) {
		ItemDto dto = new ItemDto(baseDomain);
		dto.setId(item.getId());
		dto.setName(item.getName());
		dto.setComplete(item.isComplete());
		dto.setChecklistId(item.getChecklist().getId());
		return dto;
	}
}

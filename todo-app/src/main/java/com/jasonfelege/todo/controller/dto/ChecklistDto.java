package com.jasonfelege.todo.controller.dto;

import com.jasonfelege.todo.data.domain.Checklist;

public class ChecklistDto {

	public long id;
	public String name;
	
	public static ChecklistDto fromEntity(Checklist list) {
		ChecklistDto dto = new ChecklistDto();
		dto.id = list.getId();
		dto.name = list.getName();
		return dto;
	}
}

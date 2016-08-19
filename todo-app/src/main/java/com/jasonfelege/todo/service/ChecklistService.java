package com.jasonfelege.todo.service;

import java.util.List;

import com.jasonfelege.todo.data.domain.Checklist;

public interface ChecklistService {

	List<Checklist> findByOwnerId(long userId);
	
	Checklist save(Checklist checklist);
}

package com.jasonfelege.todo.service;

import java.util.List;
import java.util.Optional;

import com.jasonfelege.todo.data.domain.Checklist;

public interface ChecklistService {

	Optional<List<Checklist>> findByOwnerId(long userId);
	
	Checklist save(Checklist checklist);
}

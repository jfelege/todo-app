package com.jasonfelege.todo.service;

import java.util.List;
import java.util.Optional;

import com.jasonfelege.todo.data.domain.Checklist;

public interface ChecklistService {

	Optional<List<Checklist>> findByOwnerId(long userId);
	
	Optional<Checklist> findById(long id);
	
	Optional<Integer> deleteById(long id);
	
	Checklist save(Checklist checklist);
}

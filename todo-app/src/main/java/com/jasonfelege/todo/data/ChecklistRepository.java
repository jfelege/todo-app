package com.jasonfelege.todo.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jasonfelege.todo.data.domain.Checklist;

public interface ChecklistRepository extends CrudRepository<Checklist, Long> {

	Optional<List<Checklist>> findByOwnerId(long userId);
	
}

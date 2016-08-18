package com.jasonfelege.todo.data;

import org.springframework.data.repository.CrudRepository;

import com.jasonfelege.todo.data.domain.Checklist;

public interface ChecklistRepository extends CrudRepository<Checklist, Long> {

  
}

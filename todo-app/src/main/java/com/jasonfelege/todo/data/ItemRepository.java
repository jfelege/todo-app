package com.jasonfelege.todo.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jasonfelege.todo.data.domain.Item;

public interface ItemRepository extends CrudRepository<Item, Long> {

	List<Item> findByChecklistId(long checklistId);

  
}

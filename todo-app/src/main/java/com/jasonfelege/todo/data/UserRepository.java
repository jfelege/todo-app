package com.jasonfelege.todo.data;

import org.springframework.data.repository.CrudRepository;

import com.jasonfelege.todo.data.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {

	    User findOneByNameIgnoreCase(String name);
	    
}

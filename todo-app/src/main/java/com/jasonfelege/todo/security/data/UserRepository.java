package com.jasonfelege.todo.security.data;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

	    User findOneByNameIgnoreCase(String name);
	    
}

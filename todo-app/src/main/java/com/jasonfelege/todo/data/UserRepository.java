package com.jasonfelege.todo.data;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jasonfelege.todo.data.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {

	Optional<User> findOneByNameIgnoreCase(String name);

	Optional<User> findOneById(long userId);
	    
}

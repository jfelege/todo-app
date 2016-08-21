package com.jasonfelege.todo.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jasonfelege.todo.data.domain.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {

    List<Role> findByName(String name);
}

package com.jasonfelege.todo;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.jasonfelege.todo.data.ChecklistRepository;
import com.jasonfelege.todo.data.ItemRepository;
import com.jasonfelege.todo.data.RoleRepository;
import com.jasonfelege.todo.data.UserRepository;
import com.jasonfelege.todo.data.domain.Role;
import com.jasonfelege.todo.data.domain.User;
import com.jasonfelege.todo.service.ChecklistService;
import com.jasonfelege.todo.service.ItemService;

@SpringBootApplication
@EnableTransactionManagement
public class Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	@Transactional
	// leave this uncommented until proper database management for
	// integration test cases is implemented.
	// @Profile("!int-test")
	public CommandLineRunner loadUserData(UserRepository userRepo, RoleRepository roleRepo, ChecklistRepository checklistRepo, ItemRepository itemRepo,
			ChecklistService checklistService, ItemService itemService) {

		return (args) -> {
			String hashed = BCrypt.hashpw("password", BCrypt.gensalt(12));

			Role role1 = new Role();
			role1.setName("ROLE_USER");
			role1 = roleRepo.save(role1);

			Role role2 = new Role();
			role2.setName("ROLE_ADMIN");
			role2 = roleRepo.save(role2);

			Set<Role> userRoles = new HashSet<Role>();
			userRoles.add(role1);

			Set<Role> adminRoles = new HashSet<Role>();
			adminRoles.add(role2);

			Set<Role> userAndAdminRoles = new HashSet<Role>();
			userAndAdminRoles.addAll(userRoles);
			userAndAdminRoles.addAll(adminRoles);

			User user1 = new User();
			user1.setName("admin");
			user1.setPassword(hashed);
			user1.setEnabled(true);
			user1.setRoles(userAndAdminRoles);
			userRepo.save(user1);

			User user2 = new User();
			user2.setName("activeuser");
			user2.setPassword(hashed);
			user2.setEnabled(true);
			user2.setRoles(userRoles);
			userRepo.save(user2);

			User user3 = new User();
			user3.setName("activeuser2");
			user3.setPassword(hashed);
			user3.setEnabled(false);
			user3.setRoles(userRoles);
			userRepo.save(user3);
			
			User user4 = new User();
			user4.setName("inactiveuser");
			user4.setPassword(hashed);
			user4.setEnabled(false);
			user4.setRoles(userRoles);
			userRepo.save(user4);
		};
	}
}

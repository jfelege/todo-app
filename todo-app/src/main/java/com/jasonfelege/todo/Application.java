package com.jasonfelege.todo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.jasonfelege.todo.data.ChecklistRepository;
import com.jasonfelege.todo.data.ItemRepository;
import com.jasonfelege.todo.data.domain.Checklist;
import com.jasonfelege.todo.data.domain.Item;
import com.jasonfelege.todo.security.data.Role;
import com.jasonfelege.todo.security.data.RoleRepository;
import com.jasonfelege.todo.security.data.User;
import com.jasonfelege.todo.security.data.UserRepository;

@SpringBootApplication
public class Application {
	private static final Logger LOG = LoggerFactory.getLogger(Application.class);
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	@Transactional
	public CommandLineRunner loadUserData(UserRepository userRepo, RoleRepository roleRepo, ChecklistRepository checklistRepo, ItemRepository itemRepo) {
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
			user1.setName("jfelege");
			user1.setPassword(hashed);
			user1.setEnabled(true);
			user1.setRoles(userAndAdminRoles);
			userRepo.save(user1);

			User user2 = new User();
			user2.setName("jsmith");
			user2.setPassword(hashed);
			user2.setEnabled(true);
			user2.setRoles(userRoles);
			userRepo.save(user2);

			User user3 = new User();
			user3.setName("jdoe");
			user3.setPassword(hashed);
			user3.setEnabled(true);
			user3.setRoles(userRoles);
			userRepo.save(user3);
			
			
			List<Item> items = new ArrayList<Item>();
			Item item1 = new Item();
			item1.setName("active item");
			item1.setComplete(false);
			Item item1b = itemRepo.save(item1);
			LOG.info("item_id={} item_name={} item_complete={}", item1b.getId(), item1b.getName(), item1b.isComplete());
			
			Item item2 = new Item();
			item2.setName("completed item");
			item2.setComplete(true);
			Item item2b = itemRepo.save(item2);
			LOG.info("item_id={} item_name={} item_complete={}", item2b.getId(), item2b.getName(), item2b.isComplete());
			
			items.add(item1);
			items.add(item2);
			
			Checklist list1 = new Checklist();
			list1.setOwner(user1);
			list1.setName("My Todo List");
			Checklist list1b = checklistRepo.save(list1);
			
			item1.setChecklist(list1);
			itemRepo.save(item1);
			
			item2.setChecklist(list1);
			itemRepo.save(item2);
			
			
			list1b = checklistRepo.findOne(1L);
			Hibernate.initialize(list1b.getItems());
			
			list1b.setItems(items);
			checklistRepo.save(list1b);
			
			LOG.info("checklist_id={} checklist_name={} checklist_size={} owner={}", list1b.getId(), list1b.getName(), (list1b.getItems() == null ? "null" : list1b.getItems().size()), list1b.getOwner());
		};
	}
}

package com.jasonfelege.todo.data.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.jasonfelege.todo.security.data.User;

@Entity
public class Checklist {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotNull
	private String name;
	
	@NotNull
	private List<Item> items;


	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", referencedColumnName="id", nullable=false)
	private User owner;
	
	//@NotNull
	//@JoinColumn(name="user_id", referencedColumnName="id")
	//private List<User> collaborators;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "Checklist [id=" + id + ", name=" + name + ", items=" + items + ", owner=" + owner + ", collaborators="
				+ "]";
	}
	
	
}

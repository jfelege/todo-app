package com.jasonfelege.todo.data.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@ManyToOne
	private Checklist checklist;
	
	public Checklist getChecklist() {
		return checklist;
	}
	
	public void setChecklist(Checklist checklist) {
		this.checklist = checklist;
	}
	

	
	
	@NotNull
	private String name;
	
	@NotNull
	private boolean complete;
	
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
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", complete=" + complete + "]";
	}
	
	
}

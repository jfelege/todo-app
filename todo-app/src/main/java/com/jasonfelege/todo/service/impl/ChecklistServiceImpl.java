package com.jasonfelege.todo.service.impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.jasonfelege.todo.data.ChecklistRepository;
import com.jasonfelege.todo.data.domain.Checklist;
import com.jasonfelege.todo.service.ChecklistService;

public class ChecklistServiceImpl implements ChecklistService {

	private final ChecklistRepository checklistRepo;
	
	public ChecklistServiceImpl(ChecklistRepository checklistRepo) {
		this.checklistRepo = checklistRepo;
	}
	
	@Transactional
	public Optional<List<Checklist>> findByOwnerId(long userId) {
		return checklistRepo.findByOwnerId(userId);
	}
	
	@Transactional
	public Optional<Checklist> findById(long id) {
		return checklistRepo.findById(id);
	}
	
	@Transactional
	public Optional<Integer> deleteById(long id) {
		return checklistRepo.deleteById(id);
	}
	
	@Transactional
	public Checklist save(Checklist checklist) {
		return checklistRepo.save(checklist);
	}
	
}

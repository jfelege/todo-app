package com.jasonfelege.todo.service;

import java.util.List;

import javax.transaction.Transactional;

import com.jasonfelege.todo.data.ChecklistRepository;
import com.jasonfelege.todo.data.domain.Checklist;

public class ChecklistServiceImpl implements ChecklistService {

	private final ChecklistRepository checklistRepo;
	
	public ChecklistServiceImpl(ChecklistRepository checklistRepo) {
		this.checklistRepo = checklistRepo;
	}
	
	@Transactional
	public List<Checklist> findByOwnerId(long userId) {
		return checklistRepo.findByOwnerId(userId);
	}
	
	@Transactional
	public Checklist save(Checklist checklist) {
		return checklistRepo.save(checklist);
	}
	
}

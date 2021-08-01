package com.IKTpreobuka.restExample.egradebook.repositories;

import org.springframework.data.repository.CrudRepository;

import com.IKTpreobuka.restExample.egradebook.entities.GradeEntity;


public interface GradeRepository extends CrudRepository<GradeEntity, Integer> {
	public GradeEntity findOneById(Integer id);
	
}

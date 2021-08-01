package com.IKTpreobuka.restExample.egradebook.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.IKTpreobuka.restExample.egradebook.entities.ParentEntity;


public interface ParentRepository extends CrudRepository<ParentEntity, Integer> {

	public ParentEntity findOneById(Integer id);
	public ParentEntity findByUsername(String name);
	List<ParentEntity> findByNameAndSurname(String name, String surname);
}

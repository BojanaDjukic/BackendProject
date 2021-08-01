package com.IKTpreobuka.restExample.egradebook.repositories;

import org.springframework.data.repository.CrudRepository;

import com.IKTpreobuka.restExample.egradebook.entities.SchoolClass;

public interface SchoolClassRepository extends CrudRepository<SchoolClass, Integer> {

	public SchoolClass findOneById(Integer id); 
	public SchoolClass findOneByName(String name); 

}

package com.IKTpreobuka.restExample.egradebook.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.IKTpreobuka.restExample.egradebook.entities.AdminEntity;

public interface AdminRepository extends CrudRepository<AdminEntity, Integer>{

	public AdminEntity findOneById(Integer id);
	List<AdminEntity> findByNameAndSurname(String name, String surname);
	
}

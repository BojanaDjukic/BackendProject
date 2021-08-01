package com.IKTpreobuka.restExample.egradebook.repositories;

import org.springframework.data.repository.CrudRepository;

import com.IKTpreobuka.restExample.egradebook.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Integer>{

	public UserEntity findOneById(Integer id);
	public UserEntity findByUsername(String username);
	public UserEntity findOneByUsername(String username);
	//public UserEntity findOneBySurnameandName(String surname,String name);
	
	
}

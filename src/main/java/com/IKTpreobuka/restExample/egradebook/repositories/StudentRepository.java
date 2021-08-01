package com.IKTpreobuka.restExample.egradebook.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.IKTpreobuka.restExample.egradebook.entities.StudentEntity;


public interface StudentRepository extends CrudRepository<StudentEntity, Integer> {
	public StudentEntity findOneById(Integer id);
	public StudentEntity findByUsername(String username);
	public List<StudentEntity> findByNameAndSurname(String name, String surname);
}
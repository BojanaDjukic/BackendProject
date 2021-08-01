package com.IKTpreobuka.restExample.egradebook.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.IKTpreobuka.restExample.egradebook.entities.TeacherEntity;

public interface TeacherRepository extends CrudRepository<TeacherEntity, Integer> {

	public TeacherEntity findOneById(Integer id);
	public TeacherEntity findByUsername(String username);
	List<TeacherEntity> findByNameAndSurname(String name, String surname);
}

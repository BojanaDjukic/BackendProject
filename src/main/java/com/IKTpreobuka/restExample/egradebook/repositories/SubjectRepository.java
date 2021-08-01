package com.IKTpreobuka.restExample.egradebook.repositories;

import org.springframework.data.repository.CrudRepository;


import com.IKTpreobuka.restExample.egradebook.entities.SubjectEntity;

public interface SubjectRepository  extends CrudRepository<SubjectEntity, Integer>  {
	
	public SubjectEntity findOneById(Integer id);
	public SubjectEntity findOneByName(String name);
	

}

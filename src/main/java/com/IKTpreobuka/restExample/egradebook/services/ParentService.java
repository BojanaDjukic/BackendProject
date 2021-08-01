package com.IKTpreobuka.restExample.egradebook.services;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.IKTpreobuka.restExample.egradebook.entities.ParentEntity;
import com.IKTpreobuka.restExample.egradebook.entities.DTO.UserEntityDTO;

public interface ParentService {

	
	public ParentEntity editParent (@PathVariable Integer id, @RequestBody ParentEntity changedParent);
	public ParentEntity createParent(UserEntityDTO userDTO);
	public ParentEntity deleteParent(Integer id); 
}

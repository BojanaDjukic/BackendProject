package com.IKTpreobuka.restExample.egradebook.services;

import java.util.List;
import java.util.Random;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.IKTpreobuka.restExample.egradebook.entities.ParentEntity;
import com.IKTpreobuka.restExample.egradebook.entities.StudentEntity;
import com.IKTpreobuka.restExample.egradebook.entities.UserRole;
import com.IKTpreobuka.restExample.egradebook.entities.DTO.UserEntityDTO;
import com.IKTpreobuka.restExample.egradebook.repositories.ParentRepository;
import com.IKTpreobuka.restExample.egradebook.utils.Encryption;

@Service
public class ParentServiceImpl implements ParentService {
	
	@Autowired
	private ParentRepository parentRepository;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	public ParentEntity editParent (Integer id, ParentEntity changedParent) {
		
		if (parentRepository.findOneById(id) != null) {
			ParentEntity pe= parentRepository.findOneById(id);
			if (changedParent.getName() != null) {
				pe.setName(changedParent.getName());
				logger.info("Name: " + pe.getName() + " changed to: " + changedParent.getName());
			}
			if (changedParent.getSurname() != null) {
				pe.setSurname(changedParent.getSurname());
				logger.info("Surname: " + pe.getSurname() + " changed to: " + changedParent.getSurname());
			}
			if (changedParent.getUsername() != null) {
				pe.setUsername(changedParent.getUsername());
				logger.info("Username: " + pe.getUsername() + " changed to: " + changedParent.getUsername());
			}
			if (changedParent.getEmail() != null) {
				pe.setEmail(changedParent.getEmail());
				logger.info("Email: " + pe.getEmail() + " changed to: " + changedParent.getEmail());
			}
			if (changedParent.getPassword() != null) {
				pe.setPassword(changedParent.getPassword());
				logger.info("Password: " + pe.getPassword() + " changed to: " + changedParent.getPassword());
			}
			if (changedParent.getRole() != null) {
				pe.setRole(changedParent.getRole());
				logger.info("Role: " + pe.getRole() + " changed to: " + changedParent.getRole());
			}
			parentRepository.save(pe);
			return pe;
			
	}
		return null;

}
	
	public ParentEntity createParent(UserEntityDTO userDTO) {
		ParentEntity pe = new ParentEntity();
		pe.setId((new Random()).nextInt());
		pe.setName(userDTO.getName());
		pe.setSurname(userDTO.getSurname());
		pe.setEmail(userDTO.getEmail());
		pe.setRole(UserRole.ROLE_PARENT);
		pe.setUsername(userDTO.getUsername());
		pe.setPassword(Encryption.getPassEncoded(userDTO.getPassword()));
		parentRepository.save(pe);
		return pe;
	}
	
	public ParentEntity deleteParent(Integer id) {
		ParentEntity pe=parentRepository.findOneById(id);
		List<StudentEntity> children=pe.getChildren();
		for(StudentEntity s: children)
		s.setParent(null);
		Hibernate.initialize(pe.getChildren());
		parentRepository.deleteById(id);
		return pe;
	}
}

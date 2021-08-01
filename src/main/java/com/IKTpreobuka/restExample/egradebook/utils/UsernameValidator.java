package com.IKTpreobuka.restExample.egradebook.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.IKTpreobuka.restExample.egradebook.entities.AdminEntity;
import com.IKTpreobuka.restExample.egradebook.entities.GradeEntity;
import com.IKTpreobuka.restExample.egradebook.entities.ParentEntity;
import com.IKTpreobuka.restExample.egradebook.entities.StudentEntity;
import com.IKTpreobuka.restExample.egradebook.entities.TeacherEntity;
import com.IKTpreobuka.restExample.egradebook.entities.UserEntity;
import com.IKTpreobuka.restExample.egradebook.entities.DTO.UserEntityDTO;
import com.IKTpreobuka.restExample.egradebook.repositories.UserRepository;

@Component
public class UsernameValidator implements Validator {

	@Autowired
	private UserRepository userRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return UserEntityDTO.class.equals(clazz) || AdminEntity.class.equals(clazz) || UserEntity.class.equals(clazz)
				|| ParentEntity.class.equals(clazz) || TeacherEntity.class.equals(clazz)
				|| StudentEntity.class.equals(clazz) 	|| GradeEntity.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserEntityDTO user = (UserEntityDTO) target;

		if (userRepository.findOneByUsername(user.getUsername()) != null) {
			errors.reject("400", "Username already exists");
		}
	}
}

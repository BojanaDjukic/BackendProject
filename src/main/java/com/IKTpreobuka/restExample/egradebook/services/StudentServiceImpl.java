package com.IKTpreobuka.restExample.egradebook.services;

import java.util.List;
import java.util.Random;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.IKTpreobuka.restExample.egradebook.entities.StudentEntity;
import com.IKTpreobuka.restExample.egradebook.entities.StudentFileEntity;
import com.IKTpreobuka.restExample.egradebook.entities.UserRole;
import com.IKTpreobuka.restExample.egradebook.entities.DTO.UserEntityDTO;
import com.IKTpreobuka.restExample.egradebook.repositories.ParentRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.SchoolClassRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.StudentRepository;
import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.IKTpreobuka.restExample.egradebook.utils.Encryption;
import com.fasterxml.jackson.annotation.JsonView;

@Service
public class StudentServiceImpl implements StudentService {

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private ParentRepository parentRepository;
	@Autowired
	private SchoolClassRepository schoolClassRepository;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	public StudentEntity createStudent(UserEntityDTO userDTO) {
		StudentEntity se = new StudentEntity();
		se.setId((new Random()).nextInt());
		se.setName(userDTO.getName());
		se.setSurname(userDTO.getSurname());
		se.setEmail(userDTO.getEmail());
		se.setRole(UserRole.ROLE_STUDENT);
		se.setUsername(userDTO.getUsername());
		se.setPassword(Encryption.getPassEncoded(userDTO.getPassword()));
		studentRepository.save(se);
		return se;
	}

	public StudentEntity editStudent(@PathVariable Integer id, @RequestBody StudentEntity changedStudent) {
		if (studentRepository.findOneById(id) != null) {
			StudentEntity se = studentRepository.findOneById(id);
			if (changedStudent.getName() != null) {
				se.setName(changedStudent.getName());
				logger.info("Name: " + se.getName() + " changed to: " + changedStudent.getName());
			}
			if (changedStudent.getSurname() != null) {
				se.setSurname(changedStudent.getSurname());
				logger.info("Surname: " + se.getSurname() + " changed to: " + changedStudent.getSurname());
			}
			if (changedStudent.getUsername() != null) {
				se.setUsername(changedStudent.getUsername());
				logger.info("Username: " + se.getUsername() + " changed to: " + changedStudent.getUsername());
			}
			if (changedStudent.getEmail() != null) {
				se.setEmail(changedStudent.getEmail());
				logger.info("Email: " + se.getEmail() + " changed to: " + changedStudent.getEmail());
			}
			if (changedStudent.getPassword() != null) {
				se.setPassword(changedStudent.getPassword());
				logger.info("Password: " + se.getPassword() + " changed to: " + changedStudent.getPassword());
			}
			if (changedStudent.getRole() != null) {
				se.setRole(changedStudent.getRole());
				logger.info("Role: " + se.getRole() + " changed to: " + changedStudent.getRole());
			}
			studentRepository.save(se);
			return se;

		}
		return null;
	}

	@JsonView(Views.Teacher.class)
	public StudentEntity parentToStudent(Integer parentId, Integer studentId) {
		if ((parentRepository.existsById(parentId) == true) && (studentRepository.existsById(studentId) == true)) {
			StudentEntity se = studentRepository.findOneById(studentId);
			se.setParent(parentRepository.findOneById(parentId));
			studentRepository.save(se);
			return se;
		}
		return null;
	}

	@JsonView(Views.Teacher.class)
	public StudentEntity classToStudent(Integer classId, Integer studentId) {
		if ((schoolClassRepository.existsById(classId) == true) && (studentRepository.existsById(studentId) == true)) {
			StudentEntity se = studentRepository.findOneById(studentId);
			se.setSchoolClass(schoolClassRepository.findOneById(classId));
			studentRepository.save(se);
			return se;
		}
		return null;
	}
	
	public StudentEntity deleteStudent(Integer id) {
		StudentEntity se = studentRepository.findOneById(id);
		List<StudentFileEntity> files=se.getFiles();
		for(StudentFileEntity s:files)
			s.setStudent(null);
		Hibernate.initialize(se.getFiles());
		studentRepository.deleteById(id);
		return se;
	}

}

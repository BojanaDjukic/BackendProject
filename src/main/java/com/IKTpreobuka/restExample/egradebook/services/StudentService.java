package com.IKTpreobuka.restExample.egradebook.services;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.IKTpreobuka.restExample.egradebook.entities.StudentEntity;
import com.IKTpreobuka.restExample.egradebook.entities.DTO.UserEntityDTO;

public interface StudentService {

	public StudentEntity editStudent (@PathVariable Integer id, @RequestBody StudentEntity changedStudent);
	public StudentEntity parentToStudent(Integer parentId,Integer studentId)throws Exception;
	public StudentEntity classToStudent(Integer classId,Integer studentId);
	public StudentEntity createStudent(UserEntityDTO userDTO);
	public StudentEntity deleteStudent(Integer id);
}

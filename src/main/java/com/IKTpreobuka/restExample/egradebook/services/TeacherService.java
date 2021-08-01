package com.IKTpreobuka.restExample.egradebook.services;

import java.io.IOException;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.IKTpreobuka.restExample.egradebook.entities.TeacherEntity;
import com.IKTpreobuka.restExample.egradebook.entities.DTO.UserEntityDTO;


public interface TeacherService {

	public TeacherEntity editTeacher(@PathVariable Integer id, @RequestBody TeacherEntity changedTeacher);
	public TeacherEntity createTeacher(UserEntityDTO userDTO);
	public void makeGradeReport(Integer teacherId) throws IOException;
	public TeacherEntity deleteTeacher(Integer id); 
}

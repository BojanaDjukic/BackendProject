package com.IKTpreobuka.restExample.egradebook.services;

import com.IKTpreobuka.restExample.egradebook.entities.StudentFileEntity;

public interface StudentFileService {

	
	public StudentFileEntity editStudentFile ( Integer id, StudentFileEntity changedStudentFile);
	public StudentFileEntity fileToStudent(Integer fileId,Integer studentId);
	public StudentFileEntity deleteStudentFile(Integer id);
	public StudentFileEntity assignStudent(Integer studentId, Integer assignedId);
}

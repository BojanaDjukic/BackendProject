package com.IKTpreobuka.restExample.egradebook.services;

import com.IKTpreobuka.restExample.egradebook.entities.AssignedEntity;

public interface AssignedService {
	
	public AssignedEntity assignSubjectToTeacher(Integer subjectId, Integer teacherId, AssignedEntity as);
	public AssignedEntity deleteAssigned(Integer id); 
}

package com.IKTpreobuka.restExample.egradebook.services;


import com.IKTpreobuka.restExample.egradebook.entities.SubjectEntity;

public interface SubjectService {
	public SubjectEntity editSubject(Integer id,SubjectEntity changedSubject);
	public SubjectEntity deleteSubject(Integer id);
}

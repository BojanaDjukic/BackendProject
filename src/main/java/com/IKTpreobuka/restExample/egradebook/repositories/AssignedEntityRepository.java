package com.IKTpreobuka.restExample.egradebook.repositories;

import org.springframework.data.repository.CrudRepository;

import com.IKTpreobuka.restExample.egradebook.entities.AssignedEntity;
import com.IKTpreobuka.restExample.egradebook.entities.SubjectEntity;
import com.IKTpreobuka.restExample.egradebook.entities.TeacherEntity;

public interface AssignedEntityRepository extends CrudRepository<AssignedEntity, Integer>{
	
	public AssignedEntity findOneById(Integer id);
    public AssignedEntity findBySubject_IdAndTeacher_IdAndFiles_Student_Id(Integer subjectId, Integer teacherId, Integer studentId);
	public AssignedEntity findBySubjectAndTeacher(SubjectEntity se, TeacherEntity te);
	public AssignedEntity findBySubject_IdAndTeacher_Id(Integer subjectId, Integer teacherId);
	
	
}

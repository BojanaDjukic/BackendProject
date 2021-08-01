package com.IKTpreobuka.restExample.egradebook.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.IKTpreobuka.restExample.egradebook.entities.StudentFileEntity;

public interface StudentFileRepository extends CrudRepository<StudentFileEntity, Integer> {
	
	public StudentFileEntity findOneById(Integer id);
	public List<StudentFileEntity> findByStudentId(Integer id);
	public StudentFileEntity findByAssigned_IdAndStudent_Id (Integer assignedId,Integer studentId);
	public List<StudentFileEntity> findByAssigned_Teacher_Id(Integer id);
	public List<StudentFileEntity> findByAssigned_Teacher_IdAndAssigned_Subject_Id(Integer teacherId,Integer subjectId );
	public List<StudentFileEntity> findByStudent_SchoolClass_Id(Integer id);
}

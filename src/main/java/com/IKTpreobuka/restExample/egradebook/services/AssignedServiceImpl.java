package com.IKTpreobuka.restExample.egradebook.services;

import java.util.List;
import java.util.Random;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.IKTpreobuka.restExample.egradebook.entities.AssignedEntity;
import com.IKTpreobuka.restExample.egradebook.entities.StudentFileEntity;
import com.IKTpreobuka.restExample.egradebook.repositories.AssignedEntityRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.SubjectRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.TeacherRepository;

@Service
public class AssignedServiceImpl implements AssignedService {

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private AssignedEntityRepository assignedRepository;

	public AssignedEntity assignSubjectToTeacher(Integer subjectId, Integer teacherId, AssignedEntity as) {
		if (subjectRepository.existsById(subjectId) && teacherRepository.existsById(teacherId)) {

			as.setId((new Random()).nextInt());
			as.setSubject(subjectRepository.findOneById(subjectId));
			as.setTeacher(teacherRepository.findOneById(teacherId));
			assignedRepository.save(as);
			return as;
		}
		return null;
	}

	public AssignedEntity deleteAssigned(Integer id) {

		AssignedEntity as = assignedRepository.findOneById(id);
		List<StudentFileEntity> students = as.getFiles();
		for (StudentFileEntity s : students)
			s.setAssigned(null);
		Hibernate.initialize(as.getFiles());
		assignedRepository.deleteById(id);
		return as;

	}


}

package com.IKTpreobuka.restExample.egradebook.services;

import java.util.List;
import java.util.Random;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.IKTpreobuka.restExample.egradebook.entities.GradeEntity;
import com.IKTpreobuka.restExample.egradebook.entities.StudentFileEntity;
import com.IKTpreobuka.restExample.egradebook.repositories.AssignedEntityRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.StudentFileRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.StudentRepository;

@Service
public class StudentFileServiceImpl implements StudentFileService {

	@Autowired
	private StudentFileRepository studentFileRepository;

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private AssignedEntityRepository assignedEntityRepository;

	public StudentFileEntity editStudentFile(Integer id, StudentFileEntity changedStudentFile) {

		if (studentFileRepository.findOneById(id) != null) {
			StudentFileEntity sfe = studentFileRepository.findOneById(id);
			studentFileRepository.save(sfe);
			return sfe;

		}
		return null;
	}

	public StudentFileEntity fileToStudent(Integer fileId, Integer studentId) {
		if ((studentFileRepository.existsById(fileId) == true) && (studentRepository.existsById(studentId) == true)) {
			StudentFileEntity sfe = studentFileRepository.findOneById(fileId);
			sfe.setStudent(studentRepository.findOneById(studentId));
			studentFileRepository.save(sfe);
			return sfe;
		}
		return null;
	}

	public StudentFileEntity deleteStudentFile(Integer id) {
		StudentFileEntity sfe = studentFileRepository.findOneById(id);
		List<GradeEntity> grades = sfe.getGrades();
		for (GradeEntity g : grades)
			g.setStudentFile(null);
		Hibernate.initialize(sfe.getGrades());
		studentFileRepository.deleteById(id);
		return sfe;

	}

	public StudentFileEntity assignStudent(Integer studentId, Integer assignedId) {
		StudentFileEntity sfe = new StudentFileEntity();
		sfe.setId((new Random()).nextInt());
		sfe.setAssigned(assignedEntityRepository.findOneById(assignedId));
		sfe.setStudent(studentRepository.findOneById(studentId));
		studentFileRepository.save(sfe);
		return sfe;
	}
}

package com.IKTpreobuka.restExample.egradebook.services;

import java.util.List;
import java.util.Random;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.IKTpreobuka.restExample.egradebook.entities.SchoolClass;
import com.IKTpreobuka.restExample.egradebook.entities.SchoolYear;
import com.IKTpreobuka.restExample.egradebook.entities.StudentEntity;
import com.IKTpreobuka.restExample.egradebook.repositories.SchoolClassRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.StudentRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.TeacherRepository;
import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Service
public class SchoolClassServiceImpl implements SchoolClassService {
	
@Autowired
private SchoolClassRepository schoolClassRepository;
@Autowired 
private TeacherRepository teacherRepository;
@Autowired 
private StudentRepository studentRepository;

private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	public SchoolClass createSchoolClass (SchoolClass sc) {
		
		 return null;
	}
	
	@JsonView(Views.Teacher.class)
	public SchoolClass elderToSC(Integer teacherId,Integer scId) {
		if ((teacherRepository.existsById(teacherId)==true) && (schoolClassRepository.existsById(scId)==true)){
			SchoolClass sc= schoolClassRepository.findOneById(scId);
			sc.setClassElder(teacherRepository.findOneById(teacherId));
			schoolClassRepository.save(sc);
			return sc;
		}
		return null;
	}
	
	
	@JsonView(Views.Teacher.class)
	public SchoolClass studentToSC(Integer studentId,Integer scId) {
		if ((studentRepository.existsById(studentId)==true) && (schoolClassRepository.existsById(scId)==true)){
			SchoolClass sc= schoolClassRepository.findOneById(scId);
			List<StudentEntity> students= sc.getStudents();
			students.add(studentRepository.findOneById(studentId));
			sc.setStudents(students);
			schoolClassRepository.save(sc);
			return sc;
		}
		return null;
	}
	
	public SchoolClass setYear(SchoolClass sc) {
		if (sc.getName().startsWith("IV") == true) 
			sc.setYear(SchoolYear.FOURTH);
		else if (sc.getName().startsWith("III") == true) 
			sc.setYear(SchoolYear.THIRD);
		else if (sc.getName().startsWith("II") == true) 
			sc.setYear(SchoolYear.SECOND);
		else if (sc.getName().startsWith("VIII") == true) 
			sc.setYear(SchoolYear.EIGHTH);
		else if (sc.getName().startsWith("VII") == true) 
			sc.setYear(SchoolYear.SEVENTH);
		else if (sc.getName().startsWith("VI") == true) 
			sc.setYear(SchoolYear.SIXTH);
		else if (sc.getName().startsWith("V") == true) 
			sc.setYear(SchoolYear.FIFTH);
	    else if (sc.getName().startsWith("I") == true) 
	    sc.setYear(SchoolYear.FIRST);
		return sc;
	}
	public SchoolClass createClass(SchoolClass sc) {
		sc=setYear(sc);
		sc.setId((new Random()).nextInt());
        schoolClassRepository.save(sc);
        return sc;
		
	}
	
	public SchoolClass editClass(Integer id, SchoolClass changedSc) {

	if (schoolClassRepository.findOneById(id) != null) {
		SchoolClass sc = schoolClassRepository.findOneById(id);
		if (changedSc.getName()!=null) {
		sc.setName(changedSc.getName());
		logger.info("Class name : " + sc.getName() + " changed to: " + changedSc.getName());
		}
		sc=setYear(sc);
		schoolClassRepository.save(sc);
		logger.info("Year auto set");
		return sc;

	}
	return null;
	}
	
	public SchoolClass deleteClass(Integer id) {
			SchoolClass sc= schoolClassRepository.findOneById(id);
			List<StudentEntity> students= sc.getStudents();
			for(StudentEntity s: students)
			s.setSchoolClass(null);
			Hibernate.initialize(sc.getStudents());
			schoolClassRepository.deleteById(id);
			return sc;
	
	}
	
	

}

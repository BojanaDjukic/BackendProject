package com.IKTpreobuka.restExample.egradebook.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.IKTpreobuka.restExample.egradebook.entities.AssignedEntity;
import com.IKTpreobuka.restExample.egradebook.entities.GradeEntity;
import com.IKTpreobuka.restExample.egradebook.entities.StudentEntity;
import com.IKTpreobuka.restExample.egradebook.entities.StudentFileEntity;
import com.IKTpreobuka.restExample.egradebook.entities.TeacherEntity;
import com.IKTpreobuka.restExample.egradebook.entities.UserRole;
import com.IKTpreobuka.restExample.egradebook.entities.DTO.UserEntityDTO;
import com.IKTpreobuka.restExample.egradebook.repositories.TeacherRepository;
import com.IKTpreobuka.restExample.egradebook.utils.Encryption;


@Service
public class TeacherServiceImpl implements TeacherService {

	@Autowired
	private TeacherRepository teacherRepository;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	public TeacherEntity createTeacher(UserEntityDTO userDTO) {
		TeacherEntity te = new TeacherEntity();
		te.setId((new Random()).nextInt());
		te.setName(userDTO.getName());
		te.setSurname(userDTO.getSurname());
		te.setEmail(userDTO.getEmail());
		te.setRole(UserRole.ROLE_TEACHER);
		te.setUsername(userDTO.getUsername());
		te.setPassword(Encryption.getPassEncoded(userDTO.getPassword()));
		teacherRepository.save(te);
		return te;
	}

	public TeacherEntity editTeacher(@PathVariable Integer id, @RequestBody TeacherEntity changedTeacher) {

		if (teacherRepository.findOneById(id) != null) {
			TeacherEntity te = teacherRepository.findOneById(id);
			if (changedTeacher.getName() != null) {
				te.setName(changedTeacher.getName());
				logger.info("Name: " + te.getName() + " changed to: " + changedTeacher.getName());
			}
			if (changedTeacher.getSurname() != null) {
				te.setSurname(changedTeacher.getSurname());
				logger.info("Surname: " + te.getSurname() + " changed to: " + changedTeacher.getSurname());
			}
			if (changedTeacher.getUsername() != null) {
				te.setUsername(changedTeacher.getUsername());
				logger.info("Username: " + te.getUsername() + " changed to: " + changedTeacher.getUsername());
			}
			if (changedTeacher.getEmail() != null) {
				te.setEmail(changedTeacher.getEmail());
				logger.info("Email: " + te.getEmail() + " changed to: " + changedTeacher.getEmail());
			}
			if (changedTeacher.getPassword() != null) {
				te.setPassword(changedTeacher.getPassword());
				logger.info("Password: " + te.getPassword() + " changed to: " + changedTeacher.getPassword());
			}
			if (changedTeacher.getRole() != null) {
				te.setRole(changedTeacher.getRole());
				logger.info("Role: " + te.getRole() + " changed to: " + changedTeacher.getRole());
			}
			teacherRepository.save(te);
			return te;

		}
		return null;
	}

	public void makeGradeReport(Integer teacherId) throws IOException {

		String tempReport = "tempReport.txt";
		List<StudentEntity> students = teacherRepository.findOneById(teacherId).getElderClass().getStudents();
		PrintWriter pw = null;

		pw = new PrintWriter(new FileOutputStream(tempReport));

		for (Iterator<StudentEntity> s = students.iterator(); s.hasNext();) {
			StudentEntity se = s.next();
			pw.println(se.getName() + " " + se.getSurname());
			pw.println(" ");
			pw.println("---------------------------------------------------");
			pw.println(" ");
			List<StudentFileEntity> files = se.getFiles();
			for (Iterator<StudentFileEntity> f = files.iterator(); f.hasNext();) {
				StudentFileEntity sfe = f.next();
				pw.println(sfe.getAssigned().getSubject().getName().toUpperCase() + ": ");
				pw.println(" ");
				pw.println("ocene:");
				List<GradeEntity> grades = sfe.getGrades();
				for (Iterator<GradeEntity> g = grades.iterator(); g.hasNext();) {
					GradeEntity ge = g.next();
					pw.print(ge.getValue() + " ");

				}
				pw.println(" ");
				pw.println(" ");
			}
		}

		if (pw != null) {
			pw.close();
		}

	}

	public TeacherEntity deleteTeacher(Integer id) {
		TeacherEntity te = teacherRepository.findOneById(id);
		List<AssignedEntity> assigned = te.getAssigned();
		for (AssignedEntity a : assigned)
			a.setTeacher(null);
		Hibernate.initialize(te.getAssigned());
		teacherRepository.deleteById(id);
		return te;
	}
}

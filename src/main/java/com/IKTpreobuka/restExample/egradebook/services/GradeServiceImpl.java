
package com.IKTpreobuka.restExample.egradebook.services;

import java.util.Calendar;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.IKTpreobuka.restExample.egradebook.entities.AssignedEntity;
import com.IKTpreobuka.restExample.egradebook.entities.GradeEntity;
import com.IKTpreobuka.restExample.egradebook.entities.TeacherEntity;
import com.IKTpreobuka.restExample.egradebook.repositories.AssignedEntityRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.GradeRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.StudentFileRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.TeacherRepository;

@Service
public class GradeServiceImpl implements GradeService {

	@Autowired
	GradeRepository gradeRepository;

	@Autowired
	private TeacherRepository teacherRepository;
	@Autowired
	private AssignedEntityRepository assignedRepository;
	@Autowired
	private StudentFileRepository studentFileRepository;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	public GradeEntity grade(GradeEntity ge, Integer studentId, Integer subjectId) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			TeacherEntity te = teacherRepository.findByUsername(authentication.getName());
			Integer teacherId = te.getId();

			if (assignedRepository.findBySubject_IdAndTeacher_IdAndFiles_Student_Id(subjectId, teacherId,
					studentId) != null) {
				AssignedEntity as = assignedRepository.findBySubject_IdAndTeacher_IdAndFiles_Student_Id(subjectId,
						teacherId, studentId);
				ge.setStudentFile(studentFileRepository.findByAssigned_IdAndStudent_Id(as.getId(), studentId));
				ge.setId((new Random()).nextInt());
				Calendar cal = Calendar.getInstance();
				ge.setDate(cal.getTime());
				gradeRepository.save(ge);

				return ge;
			}
			return null;

		} else
			return null;
	}

	public GradeEntity gradeForAdmin(GradeEntity ge, Integer studentId, Integer subjectId, Integer teacherId) throws Exception {
	
			if (assignedRepository.findBySubject_IdAndTeacher_IdAndFiles_Student_Id(subjectId, teacherId,
					studentId) != null) {
				AssignedEntity as = assignedRepository.findBySubject_IdAndTeacher_IdAndFiles_Student_Id(subjectId,
						teacherId, studentId);
				ge.setStudentFile(studentFileRepository.findByAssigned_IdAndStudent_Id(as.getId(), studentId));
				ge.setId((new Random()).nextInt());
				Calendar cal = Calendar.getInstance();
				ge.setDate(cal.getTime());
				gradeRepository.save(ge);

				return ge;
		
		} else
			return null;
	}
	public GradeEntity editGrade(Integer gradeId, GradeEntity changedGrade) {
			if (gradeRepository.findOneById(gradeId) != null) {
				GradeEntity ge =gradeRepository.findOneById(gradeId);
				if(changedGrade.getValue()!=null) {
			ge.setValue(changedGrade.getValue());
			logger.info("GradeValue : " + ge.getValue() + " changed to: " + changedGrade.getValue());
				}
				if(changedGrade.getDate()!=null) {
			ge.setDate(changedGrade.getDate());
			logger.info("GradeDate : " + ge.getDate() + " changed to: " + changedGrade.getDate());
				}
				if(changedGrade.getStudentFile()!=null) {
			ge.setStudentFile(changedGrade.getStudentFile());
			logger.info("GradeFile : " + ge.getStudentFile() + " changed to: " + changedGrade.getStudentFile());
				}
				gradeRepository.save(ge);
				return ge;
	}	else return null;
					
}

}

package com.IKTpreobuka.restExample.egradebook.controllers;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;
import com.IKTpreobuka.restExample.egradebook.controllers.utils.RESTError;
import com.IKTpreobuka.restExample.egradebook.entities.GradeEntity;
import com.IKTpreobuka.restExample.egradebook.entities.TeacherEntity;
import com.IKTpreobuka.restExample.egradebook.repositories.AssignedEntityRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.GradeRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.StudentRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.TeacherRepository;
import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.IKTpreobuka.restExample.egradebook.services.EmailService;
import com.IKTpreobuka.restExample.egradebook.services.GradeService;
import com.IKTpreobuka.restExample.egradebook.utils.IAuthenticationFacade;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/egradebook/grades")
public class GradeController {

	@Autowired
	private GradeRepository gradeRepository;
	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private GradeService gradeService;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private AssignedEntityRepository assignedRepository;
	@Autowired
	private EmailService emailService;
	@Autowired
	private IAuthenticationFacade authenticationFacade;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	protected void InitBinder(final WebDataBinder binder) {
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}

	// TODO GET- dobavi sve
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	public ResponseEntity<?> getAll() throws Exception {
		try {
			logger.info("Grade List -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
			return new ResponseEntity<Iterable<?>>(gradeRepository.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- izmeni jednu ocenu- /{id} ---
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/{gradeId}", method = RequestMethod.PUT)
	public ResponseEntity<?> editGrade(@PathVariable Integer gradeId, @Valid @RequestBody GradeEntity changedGrade,
			BindingResult result) {

		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		} else {
			try {
				if (gradeRepository.findOneById(gradeId) != null) {
					GradeEntity ge = new GradeEntity();
					ge = gradeService.editGrade(gradeId, changedGrade);
					logger.info("Grade ID: " + ge.getId() + " -- EDITED by: "
							+ authenticationFacade.getAuthentication().getName());
					return new ResponseEntity<GradeEntity>(ge, HttpStatus.OK);

				} else
					return new ResponseEntity<RESTError>(new RESTError(1, "Grade not found!"), HttpStatus.NOT_FOUND);

			} catch (Exception e) {
				return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	// TODO GET- obrisi ocenu- /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteGrade(@PathVariable Integer id) {
		try {
			if (gradeRepository.findOneById(id) != null) {
				GradeEntity ge = gradeRepository.findOneById(id);
				gradeRepository.deleteById(id);
				logger.info("Grade ID: " + ge.getId() + " -- DELETED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<GradeEntity>(ge, HttpStatus.OK);

			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "Grade not found!"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/gradestudent/{studentId}/subject/{subjectId}/teacher/{teacherId}", method = RequestMethod.GET)
	public ResponseEntity<?> grade(@Valid @RequestBody GradeEntity ge, BindingResult result,
			@PathVariable Integer studentId, @PathVariable Integer subjectId, @PathVariable Integer teacherId) {
		try {

			if (assignedRepository.findBySubject_IdAndTeacher_IdAndFiles_Student_Id(subjectId, teacherId,
					studentId) != null) {
				ge = gradeService.gradeForAdmin(ge, studentId, subjectId, teacherId);
				emailService.sendTemplateMessage(studentId, subjectId, teacherId, ge);
				logger.info("Student ID: " + studentId + " graded with mark: " + ge.getValue() + " -- GRADED by: "
						+ authenticationFacade.getAuthentication().getName());
				logger.info(
						"An email has been sent to : " + studentRepository.findOneById(studentId).getParent().getEmail()
								+ " -- SENT by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<GradeEntity>(ge, HttpStatus.OK);
			}

			else
				return new ResponseEntity<RESTError>(new RESTError(1, "Not allowed"), HttpStatus.METHOD_NOT_ALLOWED);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO POST- daj ocenu
	@Secured("ROLE_TEACHER")
	@JsonView(Views.Teacher.class)
	@RequestMapping(value = "/gradestudent/{studentId}/subject/{subjectId}", method = RequestMethod.POST)
	public ResponseEntity<?> grade(@Valid @RequestBody GradeEntity ge, BindingResult result,
			@PathVariable Integer studentId, @PathVariable Integer subjectId)throws Exception {
		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		} else {
			TeacherEntity te = teacherRepository.findByUsername(authenticationFacade.getAuthentication().getName());
			Integer teacherId = te.getId();
			if (!(authenticationFacade.getAuthentication() instanceof AnonymousAuthenticationToken)) {

				if (assignedRepository.findBySubject_IdAndTeacher_IdAndFiles_Student_Id(subjectId, teacherId,
						studentId) != null) {
					ge = gradeService.grade(ge, studentId, subjectId);
					emailService.sendTemplateMessage(studentId, subjectId, teacherId, ge);
					logger.info("Student ID: " + studentId + " graded with mark: " + ge.getValue() + " -- GRADED by: "
							+ authenticationFacade.getAuthentication().getName());
					logger.info("An email has been sent to : "
							+ studentRepository.findOneById(studentId).getParent().getEmail() + " -- SENT by: "
							+ authenticationFacade.getAuthentication().getName());
					return new ResponseEntity<GradeEntity>(ge, HttpStatus.OK);
				}

				else
					return new ResponseEntity<RESTError>(new RESTError(1, "Not allowed"),
							HttpStatus.METHOD_NOT_ALLOWED);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "Not!!"), HttpStatus.METHOD_NOT_ALLOWED);

		}
	}

	// TODO PUT- ucitelj menja ocenu
	@Secured("ROLE_TEACHER")
	@JsonView(Views.Teacher.class)
	@RequestMapping(value = "/changeGrade/{gradeId}", method = RequestMethod.PUT)
	public ResponseEntity<?> changeGrade(@Valid @RequestBody GradeEntity changedGrade, BindingResult result,
			@PathVariable Integer gradeId) {

		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		} else {
			TeacherEntity te = teacherRepository.findByUsername(authenticationFacade.getAuthentication().getName());
			Integer teacherId = te.getId();
			if (!(authenticationFacade.getAuthentication() instanceof AnonymousAuthenticationToken)) {
				GradeEntity ge = gradeRepository.findOneById(gradeId);
				Integer subjectId = ge.getStudentFile().getAssigned().getSubject().getId();
				Integer studentId = ge.getStudentFile().getStudent().getId();

				if (assignedRepository.findBySubject_IdAndTeacher_IdAndFiles_Student_Id(subjectId, teacherId,studentId) != null) {
					gradeService.editGrade(gradeId, changedGrade);
					logger.info("Grade ID: " + gradeId + " changed! " + " -- MADE by: "
							+ authenticationFacade.getAuthentication().getName());
					return new ResponseEntity<GradeEntity>(ge, HttpStatus.OK);
				} else
					return new ResponseEntity<RESTError>(new RESTError(1, "Not allowed"),
							HttpStatus.METHOD_NOT_ALLOWED);
		} else
			return new ResponseEntity<RESTError>(new RESTError(1, "Not allowed"),
					HttpStatus.METHOD_NOT_ALLOWED);
		}	
	}

	// TODO PUT- ucitelj brise ocenu
	@Secured("ROLE_TEACHER")
	@JsonView(Views.Teacher.class)
	@RequestMapping(value = "/delete/grade/{gradeId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteGradeByTeacher(@PathVariable Integer gradeId) {
		try {
			TeacherEntity te = teacherRepository.findByUsername(authenticationFacade.getAuthentication().getName());
			Integer teacherId = te.getId();
			// Authentication authentication =
			// SecurityContextHolder.getContext().getAuthentication();
			if (!(authenticationFacade.getAuthentication() instanceof AnonymousAuthenticationToken)) {
				GradeEntity ge = gradeRepository.findOneById(gradeId);
				Integer subjectId = ge.getStudentFile().getAssigned().getSubject().getId();
				Integer studentId = ge.getStudentFile().getStudent().getId();

				if (assignedRepository.findBySubject_IdAndTeacher_IdAndFiles_Student_Id(subjectId, teacherId,
						studentId) != null) {
					gradeRepository.deleteById(gradeId);
					logger.info("Grade ID: " + gradeId + " -- DELETED by: "
							+ authenticationFacade.getAuthentication().getName());
					return new ResponseEntity<GradeEntity>(ge, HttpStatus.OK);
				}

				else
					return new ResponseEntity<RESTError>(new RESTError(1, "Not allowed"),
							HttpStatus.METHOD_NOT_ALLOWED);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "Not allowed!!"), HttpStatus.METHOD_NOT_ALLOWED);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

}

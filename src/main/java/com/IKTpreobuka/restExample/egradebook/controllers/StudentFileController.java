package com.IKTpreobuka.restExample.egradebook.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.hibernate.Hibernate;
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
import com.IKTpreobuka.restExample.egradebook.entities.StudentFileEntity;
import com.IKTpreobuka.restExample.egradebook.repositories.AssignedEntityRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.SchoolClassRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.StudentFileRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.StudentRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.TeacherRepository;
import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.IKTpreobuka.restExample.egradebook.services.StudentFileService;
import com.IKTpreobuka.restExample.egradebook.utils.IAuthenticationFacade;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/egradebook/studentfiles")
public class StudentFileController {

	@Autowired
	private StudentFileRepository studentFileRepository;

	@Autowired
	private AssignedEntityRepository assignedEntityRepository;
	@Autowired
	private SchoolClassRepository schoolClassRepository;
	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private StudentFileService studentFileService;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private IAuthenticationFacade authenticationFacade;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	protected void InitBinder(final WebDataBinder binder) {
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}

	// TODO POST- napravi novi fajl
	@JsonView(Views.Teacher.class)
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<?> createFile(@Valid @RequestBody StudentFileEntity sfe, BindingResult result) {
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else {
			sfe.setId((new Random()).nextInt());
			studentFileRepository.save(sfe);
			logger.info("StudentFile ID: " + sfe.getId() + " -- CREATED by: "
					+ authenticationFacade.getAuthentication().getName());
			return new ResponseEntity<StudentFileEntity>(sfe, HttpStatus.CREATED);
		}
	}

	// TODO GET- dobavi sve
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@JsonView(Views.Teacher.class)
	public ResponseEntity<?> getAll() throws Exception {
		try {
			logger.info("StudentFile List -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
			return new ResponseEntity<Iterable<?>>(studentFileRepository.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- dobavi jedan fajl- /{id}
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getOne(@PathVariable Integer id) {

		try {
			StudentFileEntity sfe = studentFileRepository.findOneById(id);
			if (studentFileRepository.findOneById(id) != null) {
				logger.info("StudentFile ID: " + sfe.getId() + " -- FETCHED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<StudentFileEntity>(sfe, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "File can not be found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- izmeni jedan fajl- /{id} //ne radi nista- nema sta da se menja
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> editFile(@PathVariable Integer id, @RequestBody StudentFileEntity changedFile) {
		try {
			if (studentFileRepository.findOneById(id) != null) {
				StudentFileEntity sfe = studentFileService.editStudentFile(id, changedFile);
				logger.info("StudentFile: " + sfe.getId() + " -- EDITED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<StudentFileEntity>(sfe, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "File can not be found"), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO DELETE - obrisi fajl - /{id}
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteFile(@PathVariable Integer id) {

		try {
			StudentFileEntity sfe = studentFileRepository.findOneById(id);
			if (studentFileRepository.findOneById(id) != null) {
				studentFileService.deleteStudentFile(id);
				logger.info("StudentFile ID: " + sfe.getId() + " -- DELETED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<StudentFileEntity>(sfe, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "File can not be found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- dodeli fajl uceniku- /
	@RequestMapping(value = "/file/{fileId}/toStudent/{studentId}", method = RequestMethod.GET)
	public ResponseEntity<?> fileToStudent(@PathVariable Integer fileId, @PathVariable Integer studentId)
			throws Exception {
		try {
			if ((studentFileRepository.existsById(fileId) == true)
					&& (studentRepository.existsById(studentId) == true)) {
				StudentFileEntity sfe = studentFileService.fileToStudent(fileId, studentId);
				logger.info("StudentFile ID: " + sfe.getId() + " assigned to Student ID: " + studentId + "-- MADE by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<StudentFileEntity>(sfe, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "File or Student not found"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- fajlovi za odeljenje kojem predaje- /{id}
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.GET, value = "/gradesbyclass/{classId}")
	public ResponseEntity<?> getGradesForClass(@PathVariable Integer classId) {

		if (schoolClassRepository.findById(classId) != null) {

			if (studentFileRepository.findByStudent_SchoolClass_Id(classId) != null) {
				List<StudentFileEntity> files = studentFileRepository.findByStudent_SchoolClass_Id(classId);
				List<StudentFileEntity> assignedFiles = new ArrayList<>();//Collections.<StudentFileEntity>emptyList();
				Hibernate.initialize(assignedFiles);

				if (!(authenticationFacade.getAuthentication() instanceof AnonymousAuthenticationToken)) {
					Integer teacherId = teacherRepository
							.findByUsername(authenticationFacade.getAuthentication().getName()).getId();
					for (StudentFileEntity s : files)
						if (s.getAssigned().getTeacher().getId() == teacherId) {
							assignedFiles.add(s);
							logger.info("Fetched files for Class ID: " + classId + " -- FETCHED by: "
									+ authenticationFacade.getAuthentication().getName());
							return new ResponseEntity<List<StudentFileEntity>>(assignedFiles, HttpStatus.OK);
						} else
							return new ResponseEntity<RESTError>(new RESTError(1, "Not allowed"), HttpStatus.NOT_FOUND);
				} else
					return new ResponseEntity<RESTError>(new RESTError(1, "Not allowed"), HttpStatus.FORBIDDEN);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "No assigned files for this Class!"),
						HttpStatus.NOT_FOUND);
		} else
			return new ResponseEntity<RESTError>(new RESTError(1, "SchoolClass not found!"), HttpStatus.NOT_FOUND);
		return null;

	}

	// TODO GET- dodeli ucenika predmetu i ucitelju(assigned
	@RequestMapping(value = "/student/{studentId}/assigned/{assignedId}", method = RequestMethod.POST)
	@Secured("ROLE_ADMIN")
	public ResponseEntity<?> assignStudentToAssigned(@PathVariable Integer studentId,
			@PathVariable Integer assignedId) {

		try {
			if ((studentRepository.existsById(studentId)) && (assignedEntityRepository.existsById(assignedId))) {

				if (studentFileRepository.findByAssigned_IdAndStudent_Id(assignedId, studentId) == null) {
					StudentFileEntity sfe = studentFileService.assignStudent(studentId, assignedId);
					logger.info("Student ID: " + studentId + " assigned to Assigned ID: " + assignedId + " -- MADE by: "
							+ authenticationFacade.getAuthentication().getName());
					return new ResponseEntity<StudentFileEntity>(sfe, HttpStatus.CREATED);
				} else
					return new ResponseEntity<RESTError>(
							new RESTError(1, " Student already assigned to this teacher and subject!"),
							HttpStatus.NOT_FOUND);

			} else
				return new ResponseEntity<RESTError>(new RESTError(1, " Student or Assigned not found!"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

package com.IKTpreobuka.restExample.egradebook.controllers;

import java.util.stream.Collectors;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.IKTpreobuka.restExample.egradebook.controllers.utils.RESTError;
import com.IKTpreobuka.restExample.egradebook.entities.SchoolClass;
import com.IKTpreobuka.restExample.egradebook.repositories.SchoolClassRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.TeacherRepository;
import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.IKTpreobuka.restExample.egradebook.services.SchoolClassService;
import com.IKTpreobuka.restExample.egradebook.utils.IAuthenticationFacade;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/egradebook/schoolclasses")
public class SchoolClassController {

	@Autowired
	private SchoolClassRepository schoolClassRepository;
	@Autowired
	private SchoolClassService schoolClassService;
	@Autowired
	private TeacherRepository teacherRepository;
	@Autowired
	private IAuthenticationFacade authenticationFacade;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	@org.springframework.web.bind.annotation.InitBinder
	protected void InitBinder(final WebDataBinder binder) {
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	public ResponseEntity<?> createClass(@Valid @RequestBody SchoolClass sc, BindingResult result) {
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);

		else {

			if (schoolClassRepository.findOneByName(sc.getName()) == null) {
				schoolClassService.createClass(sc);
				logger.info("SchoolClass ID: " + sc.getId() + " -- CREATED by: " + authenticationFacade.getAuthentication().getName());	
				return new ResponseEntity<SchoolClass>(sc, HttpStatus.CREATED);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "Class with this name already exists!"),
						HttpStatus.NOT_ACCEPTABLE);
		}
	}

	// TODO GET- dobavi sve
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@JsonView(Views.Teacher.class)
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@JsonIgnoreProperties("subjects")
	public ResponseEntity<?> getAll() {
		try {
			logger.info("SchoolClass List -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
			return new ResponseEntity<Iterable<?>>(schoolClassRepository.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- dobavi sve PARENT/STUDENT VIEW
	@RequestMapping(value = "/parentview", method = RequestMethod.GET)
	@JsonView(Views.Private.class)
	@Secured({ "ROLE_PARENT", "ROLE_STUDENT" })
	@JsonIgnoreProperties("subjects")
	public ResponseEntity<?> getAllPS() {
		try {
			logger.info("SchoolClass List -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
			return new ResponseEntity<Iterable<?>>(schoolClassRepository.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// TODO GET- dobavi jedno odeljenje- /{id}
	@Secured({"ROLE_ADMIN","ROLE_TEACHER"})
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getOne(@PathVariable Integer id) {

		try {
			SchoolClass sc = schoolClassRepository.findOneById(id);
			if (schoolClassRepository.findOneById(id) != null) {
				logger.info("SchoolClass ID: " + sc.getId() + " -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<SchoolClass>(sc, HttpStatus.OK);
			}
			else
				return new ResponseEntity<RESTError>(new RESTError(1, "SchoolClass not found!"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	// TODO GET- dodeli razrednog odeljenju- /{id}
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(value = "/elder/{teacherId}/toschoolclass/{scId}", method = RequestMethod.PUT)
	public ResponseEntity<?> elderToSC(@PathVariable Integer teacherId, @PathVariable Integer scId) throws Exception {
		try {
			if ((teacherRepository.existsById(teacherId) == true) && (schoolClassRepository.existsById(scId) == true)) {
				SchoolClass sc = schoolClassService.elderToSC(teacherId, scId);
				logger.info("SchoolClass ID: " + sc.getId() + "connected to Elder(Teacher) ID: " + teacherId + " -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<SchoolClass>(sc, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "Teacher or SchoolClass not found!"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/*// TODO GET- dodeli studenta odeljenju- /{id} // dodaje se iz student controller-a
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(value = "/student/{studentId}/toschoolclass/{scId}", method = RequestMethod.PUT)
	public ResponseEntity<?> studentToSC(@PathVariable Integer studentId, @PathVariable Integer scId) throws Exception {
		try {
			if ((studentRepository.existsById(studentId) == true) && (schoolClassRepository.existsById(scId) == true)) {
				SchoolClass sc = schoolClassService.studentToSC(studentId, scId);
				return new ResponseEntity<SchoolClass>(sc, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "Student or SchoolClass not found!"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}*/
	
	// TODO PUT- izmeni odeljenje- /{id}
		@Secured({"ROLE_ADMIN","ROLE_TEACHER"})
		@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
		public ResponseEntity<?> editSClass(@PathVariable Integer id, @RequestBody SchoolClass changedClass) {
			try {
				if (schoolClassRepository.findOneById(id) != null) {
					SchoolClass sc = schoolClassService.editClass(id, changedClass);
					logger.info("SchoolClass ID: " + sc.getId() +  " -- EDITED by: " + authenticationFacade.getAuthentication().getName());
					return new ResponseEntity<SchoolClass>(sc, HttpStatus.OK);
				} else
					return new ResponseEntity<RESTError>(new RESTError(1, "SchoolClass not found!"), HttpStatus.NOT_FOUND);
			} catch (Exception e) {
				return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

	// TODO GET- obrisi odeljenje- /{id}
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteClass(@PathVariable Integer id) {

		try {
			SchoolClass sc = schoolClassRepository.findOneById(id);
			if (schoolClassRepository.findOneById(id) != null) {
				schoolClassService.deleteClass(id);
				logger.info("SchoolClass ID: " + sc.getId() + " -- DELETED by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<SchoolClass>(sc, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "SchoolClass not found!"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}

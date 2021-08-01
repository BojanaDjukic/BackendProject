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
import com.IKTpreobuka.restExample.egradebook.entities.AssignedEntity;
import com.IKTpreobuka.restExample.egradebook.entities.StudentFileEntity;
import com.IKTpreobuka.restExample.egradebook.repositories.AssignedEntityRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.StudentFileRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.SubjectRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.TeacherRepository;
import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.IKTpreobuka.restExample.egradebook.services.AssignedService;
import com.IKTpreobuka.restExample.egradebook.utils.IAuthenticationFacade;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/egradebook/assigned")
public class AssignedController {

	@Autowired
	private AssignedEntityRepository assignedRepository;
	@Autowired
	private SubjectRepository subjectRepository;
	@Autowired
	private TeacherRepository teacherRepository;
	@Autowired
	private StudentFileRepository studentFileRepository;
	@Autowired
	private AssignedService assignedService;
	@Autowired
	private IAuthenticationFacade authenticationFacade;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@org.springframework.web.bind.annotation.InitBinder
	protected void InitBinder(final WebDataBinder binder) {

	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}

	
	// TODO GET- dodeli predmet ucitelju
	@RequestMapping(value = "/subject/{subjectId}/teacher/{teacherId}", method = RequestMethod.POST)
	@Secured("ROLE_ADMIN")
	public ResponseEntity<?> assignSubjectToTeacher(@PathVariable Integer subjectId, @PathVariable Integer teacherId,
			@Valid @RequestBody AssignedEntity as, BindingResult result) {
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);

		else {
			if (subjectRepository.existsById(subjectId) && teacherRepository.existsById(teacherId)) {
				if(assignedRepository.findBySubject_IdAndTeacher_Id(subjectId, teacherId)==null) {

				as = assignedService.assignSubjectToTeacher(subjectId, teacherId, as);
				as = assignedService.assignSubjectToTeacher(subjectId, teacherId, as);
				logger.info("Subject ID: " + subjectId + " assigned to Teacher ID: " + teacherId+ " -- MADE by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<AssignedEntity>(as, HttpStatus.CREATED);
				}else return new ResponseEntity<RESTError>(new RESTError(1, " This subject is already assigned to this teacher!"),
						HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<RESTError>(new RESTError(1, "Teacher or Subjest not found!"),
					HttpStatus.NOT_FOUND);
		}
	}

	// TODO GET- dobavi sve
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	public ResponseEntity<?> getAll() {
		try {
			logger.info("Assigned List -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
			return new ResponseEntity<Iterable<?>>(assignedRepository.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// TODO get- dobavi jedan 
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getOne(@PathVariable Integer id) {

		try {
			AssignedEntity as = assignedRepository.findOneById(id);
			if (assignedRepository.findOneById(id) != null) {
				logger.info("Assigned ID: " + as.getId() + " -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<AssignedEntity>(as, HttpStatus.OK);
			}
			else
				return new ResponseEntity<RESTError>(new RESTError(1, "Assigned not found"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- dodeli fajl 
	@RequestMapping(value = "/file/{fileId}/assigned/{assignedId}", method = RequestMethod.PUT)
	@JsonView(Views.Teacher.class)
	@Secured("ROLE_ADMIN")
	public ResponseEntity<?> assignFile(@PathVariable Integer fileId, @PathVariable Integer assignedId) {
		try {
			if (studentFileRepository.existsById(fileId) && assignedRepository.existsById(assignedId)) {
				StudentFileEntity sfe = studentFileRepository.findOneById(fileId);
				sfe.setAssigned(assignedRepository.findOneById(assignedId));
				studentFileRepository.save(sfe);
				logger.info("StudentFile ID: " + fileId + " connected to Assigned ID: " + assignedId+ " -- MADE by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<StudentFileEntity>(sfe, HttpStatus.OK);
			}
			return new ResponseEntity<RESTError>(new RESTError(1, "File or assigned not found!"), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public AssignedEntity findByTeacherAndSubject(Integer teacherId, Integer subjectId) {
		AssignedEntity as = assignedRepository.findBySubject_IdAndTeacher_Id(subjectId, teacherId);
		logger.info("Search Assigned by teacher ID: " + teacherId + " and Subject ID: " + subjectId+ " -- MADE by: " + authenticationFacade.getAuthentication().getName());
		return as;
	}
	
	// TODO DELETE- izbrisi fajl 
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id) {

		try {
			AssignedEntity as = assignedRepository.findOneById(id);
			if (assignedRepository.findOneById(id) != null) {
				assignedService.deleteAssigned(id);
				logger.info("Assigned ID: " + as.getId()  + " -- DELETED by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<AssignedEntity>(as, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "Assigned not found!"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}


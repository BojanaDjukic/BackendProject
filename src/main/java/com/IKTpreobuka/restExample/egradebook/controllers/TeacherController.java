package com.IKTpreobuka.restExample.egradebook.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.IKTpreobuka.restExample.egradebook.controllers.utils.RESTError;
import com.IKTpreobuka.restExample.egradebook.entities.SchoolClass;
import com.IKTpreobuka.restExample.egradebook.entities.StudentFileEntity;
import com.IKTpreobuka.restExample.egradebook.entities.TeacherEntity;
import com.IKTpreobuka.restExample.egradebook.entities.UserEntity;
import com.IKTpreobuka.restExample.egradebook.entities.DTO.UserEntityDTO;
import com.IKTpreobuka.restExample.egradebook.repositories.StudentFileRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.TeacherRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.UserRepository;
import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.IKTpreobuka.restExample.egradebook.services.TeacherService;
import com.IKTpreobuka.restExample.egradebook.utils.IAuthenticationFacade;
import com.IKTpreobuka.restExample.egradebook.utils.PasswordValidator;
import com.IKTpreobuka.restExample.egradebook.utils.UsernameValidator;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/egradebook/users/teachers")
public class TeacherController {

	@Autowired
	private TeacherRepository teacherRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordValidator passwordValidator;
	@Autowired
	private UsernameValidator usernameValidator;
	@Autowired
	private TeacherService teacherService;
	@Autowired
	private StudentFileRepository studentFileRepository;
	@Autowired
	private IAuthenticationFacade authenticationFacade;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	private final String gradeReport = "C:\\Users\\bdjuk\\workspaceIKTPreobuka\\e-gradebook\\tempReport.txt";

	@org.springframework.web.bind.annotation.InitBinder
	protected void InitBinder(final WebDataBinder binder) {
		binder.addValidators(passwordValidator,usernameValidator);
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}

	// TODO POST- napravi novog ucitelja
	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@Valid@RequestBody UserEntityDTO userDTO, BindingResult result) {
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else { passwordValidator.validate(userDTO, result);
				usernameValidator.validate(userDTO, result);
		}
			TeacherEntity te = teacherService.createTeacher(userDTO);
				logger.info("Teacher ID: " + te.getId() + ", username: " + te.getUsername() + " -- CREATED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<TeacherEntity>(te, HttpStatus.CREATED);
	}

	// TODO GET- dobavi sve
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> getAll() throws Exception {
		try {
			logger.info("Teacher List -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
			return new ResponseEntity<Iterable<?>>(teacherRepository.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- dobavi jednog ucitelja- /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getOne(@PathVariable Integer id) {

		try {
			TeacherEntity te = teacherRepository.findOneById(id);
			if (teacherRepository.findOneById(id) != null) {
				logger.info("Teacher ID: " + te.getId() + ", username: " + te.getUsername() + " -- FETCHED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<UserEntity>(te, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "User can not be found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- izmeni ucitelja- /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> editTeacher(@PathVariable Integer id, @RequestBody TeacherEntity changedTeacher) {
		try {
			if (teacherRepository.findOneById(id) != null) {
				if(userRepository.findOneByUsername(changedTeacher.getUsername())==null) {
				TeacherEntity te = teacherService.editTeacher(id, changedTeacher);
				logger.info("Teacher ID: " + te.getId() + " -- EDITED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<TeacherEntity>(te, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "Username already in use!"),
						HttpStatus.NOT_FOUND);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "User can not be found"), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO DELETE - obrisi ucitelja - /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id) {

		try {
			TeacherEntity te = teacherRepository.findOneById(id);
			if (teacherRepository.findOneById(id) != null) {
				teacherService.deleteTeacher(id);
				logger.info("Teacher ID: " + te.getId() + ", username: " + te.getUsername() + " -- DELETED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<TeacherEntity>(te, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "User can not be found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- nadji po imenu i prezimenu- /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/byNameAndSurname", method = RequestMethod.GET)
	public ResponseEntity<?> getOne(@RequestParam String name, @RequestParam String surname) {

		try {
			List<TeacherEntity> teachers = teacherRepository.findByNameAndSurname(name, surname);
			if (!teacherRepository.findByNameAndSurname(name, surname).isEmpty()) {
				logger.info("Searhed teachers with name: " + name + "and surname: " + surname + " -- FETCHED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<List<TeacherEntity>>(teachers, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "User can not be found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}



	// TODO GET- dobavi Moje odeljenje
	@Secured("ROLE_TEACHER")
	@RequestMapping(value = "/getmyclass", method = RequestMethod.GET)
	@JsonView(Views.Teacher.class)
	public ResponseEntity<?> getMyClass() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (!(authentication instanceof AnonymousAuthenticationToken)) {
				Integer teacherId = teacherRepository.findByUsername(authentication.getName()).getId();
				SchoolClass sc = teacherRepository.findOneById(teacherId).getElderClass();
				logger.info("Fetched ElderClass by Teacher ID: " + teacherId + " -- FETCHED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<SchoolClass>(sc, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "Class not found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_TEACHER")
	@JsonView(Views.Teacher.class)
	@RequestMapping(value = "/myclass/report", method = RequestMethod.GET)
	public ResponseEntity<Object> downloadFile() throws IOException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			Integer teacherId = teacherRepository.findByUsername(authentication.getName()).getId();
			teacherService.makeGradeReport(teacherId);
			String filename = gradeReport;
			File file = new File(filename);
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");

			ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headers).contentLength(file.length())
					.contentType(MediaType.parseMediaType("application/txt")).body(resource);
			logger.info("Fetched Report for MyClass by Teacher ID: " + teacherId + " -- FETCHED by: "
					+ authenticationFacade.getAuthentication().getName());

			return responseEntity;
		} else
			return null;
	}
	
	// TODO GET- dobavi sve ocene koje je dao ulogovani ucitelj
	@Secured("ROLE_TEACHER")
	@JsonView(Views.Teacher.class)
	@RequestMapping(value = "/getmygrades", method = RequestMethod.GET)
	public ResponseEntity<?> getMyGrades() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (!(authentication instanceof AnonymousAuthenticationToken)) {
				Integer teacherId = teacherRepository.findByUsername(authentication.getName()).getId();
				List<StudentFileEntity> files=studentFileRepository.findByAssigned_Teacher_Id(teacherId);
				logger.info("Fetched Files by Teacher ID: " + teacherId + " -- FETCHED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<List<StudentFileEntity>>(files, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "No assigned files"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// TODO GET- dobavi sve ocene koje je dao ulogovani ucitelj za predmet {subjectId}
	@Secured("ROLE_TEACHER")
	@JsonView(Views.Teacher.class)
	@RequestMapping(value = "/getmygrades/subject/{subjectId}", method = RequestMethod.GET)
	public ResponseEntity<?> getMySubjectGrades(@PathVariable Integer subjectId) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (!(authentication instanceof AnonymousAuthenticationToken)) {
				Integer teacherId = teacherRepository.findByUsername(authentication.getName()).getId();
				List<StudentFileEntity> files=studentFileRepository.findByAssigned_Teacher_IdAndAssigned_Subject_Id(teacherId, subjectId);
				logger.info("Fetched Files for Subject ID: " + subjectId + " and Teacher ID: " + teacherId + " -- FETCHED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<List<StudentFileEntity>>(files, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "No assigned files"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}

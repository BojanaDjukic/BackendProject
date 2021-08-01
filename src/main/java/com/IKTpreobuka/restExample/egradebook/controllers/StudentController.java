package com.IKTpreobuka.restExample.egradebook.controllers;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.IKTpreobuka.restExample.egradebook.controllers.utils.RESTError;
import com.IKTpreobuka.restExample.egradebook.entities.ParentEntity;
import com.IKTpreobuka.restExample.egradebook.entities.StudentEntity;
import com.IKTpreobuka.restExample.egradebook.entities.DTO.UserEntityDTO;
import com.IKTpreobuka.restExample.egradebook.repositories.ParentRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.SchoolClassRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.StudentRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.UserRepository;
import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.IKTpreobuka.restExample.egradebook.services.StudentService;
import com.IKTpreobuka.restExample.egradebook.utils.IAuthenticationFacade;
import com.IKTpreobuka.restExample.egradebook.utils.PasswordValidator;
import com.IKTpreobuka.restExample.egradebook.utils.UsernameValidator;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/egradebook/users/students")
public class StudentController {
	
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private ParentRepository parentRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordValidator passwordValidator;
	@Autowired
	private UsernameValidator usernameValidator;
	@Autowired
	private StudentService studentService;
	@Autowired
	private SchoolClassRepository schoolClassRepository;
	@Autowired
	private IAuthenticationFacade authenticationFacade;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@org.springframework.web.bind.annotation.InitBinder
	protected void InitBinder(final WebDataBinder binder) {
		binder.addValidators(passwordValidator,usernameValidator);
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}

	// TODO POST- napravi novog ucenika
	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@Valid @RequestBody UserEntityDTO userDTO, BindingResult result) {
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else { passwordValidator.validate(userDTO, result);
				usernameValidator.validate(userDTO, result);
		}
				StudentEntity se= studentService.createStudent(userDTO);
				logger.info("Student ID: " + se.getId() + ", username: " + se.getUsername() + " -- CREATED by: " + authenticationFacade.getAuthentication().getName());	
				return new ResponseEntity<StudentEntity>(se, HttpStatus.CREATED);
	}
	
	// TODO GET- dobavi sve
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> getAll() throws Exception {
		try {
			logger.info("Student List -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
			return new ResponseEntity<Iterable<?>>(studentRepository.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- dobavi jednog ucenika- /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getOne(@PathVariable Integer id) {

		try {
			StudentEntity se = studentRepository.findOneById(id);
			if (studentRepository.findOneById(id) != null) {
				logger.info("Student ID: " + se.getId() + ", username: " + se.getUsername() + " -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<StudentEntity>(se, HttpStatus.OK);
			}
			else
				return new ResponseEntity<RESTError>(new RESTError(1, "User can not be found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- izmeni ucenika- /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> editStudent(@PathVariable Integer id, @RequestBody StudentEntity changedStudent) {
		try {
			if (studentRepository.findOneById(id) != null) {
				if(userRepository.findOneByUsername(changedStudent.getUsername())==null) {
				StudentEntity se = studentService.editStudent(id, changedStudent);
				logger.info("Student ID: " + se.getId() +  " -- EDITED by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<StudentEntity>(se, HttpStatus.OK);
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
	
		// TODO DELETE - obrisi ucenika - /{id}
		@Secured("ROLE_ADMIN")
		@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
		public ResponseEntity<?> deleteUser(@PathVariable Integer id) {

			try {
				StudentEntity se = studentRepository.findOneById(id);
				if (studentRepository.findOneById(id) != null) {
					studentService.deleteStudent(id);
					logger.info("Student ID: " + se.getId() + ", username: " + se.getUsername() + " -- DELETED by: " + authenticationFacade.getAuthentication().getName());
					return new ResponseEntity<StudentEntity>(se, HttpStatus.OK);
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
				List<StudentEntity> students = studentRepository.findByNameAndSurname(name, surname);
				if (!studentRepository.findByNameAndSurname(name, surname).isEmpty()) {
					logger.info("Students with name: " + name + "and surname: " + surname + " -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
					return new ResponseEntity<List<StudentEntity>>(students, HttpStatus.OK);
				}

				else
					return new ResponseEntity<RESTError>(new RESTError(1, "User can not be found"), HttpStatus.NOT_FOUND);

			} catch (Exception e) {
				return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		// TODO GET- dodeli roditelja uceniku- /{id}
		@Secured("ROLE_ADMIN")
		@RequestMapping(value = "/parent/{parentId}/toStudent/{studentId}", method = RequestMethod.GET)
		public ResponseEntity<?> parentToStudent(@PathVariable Integer parentId, @PathVariable Integer studentId) throws Exception{
			try {
				if ((parentRepository.existsById(parentId)==true) && (studentRepository.existsById(studentId)==true)) {
					StudentEntity se=studentService.parentToStudent(parentId, studentId);
					logger.info("Student ID: " + studentId + "connected to Parent ID: " + parentId + " -- CONNECTED by: " + authenticationFacade.getAuthentication().getName());
					return new ResponseEntity<StudentEntity>(se, HttpStatus.OK);
				}
				else
					return new ResponseEntity<RESTError>(new RESTError(1, "User can not be found"), HttpStatus.NOT_FOUND);

			} catch (Exception e) {
				return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		// TODO GET- dodeli odeljenje uceniku- /{id}
		@Secured("ROLE_ADMIN")
		@RequestMapping(value = "/sc/{scId}/toStudent/{studentId}", method = RequestMethod.GET)
		public ResponseEntity<?> scToStudent(@PathVariable Integer scId, @PathVariable Integer studentId) throws Exception{
			try {
				if ((schoolClassRepository.existsById(scId)==true) && (studentRepository.existsById(studentId)==true)) {
					StudentEntity se=studentService.classToStudent(scId, studentId);
					logger.info("Student ID: " + studentId + "connected to SchoolClass ID: " + scId + " -- CONNECTED by: " + authenticationFacade.getAuthentication().getName());
					return new ResponseEntity<StudentEntity>(se, HttpStatus.OK);
				}
				else
					return new ResponseEntity<RESTError>(new RESTError(1, "Student or Class not found"), HttpStatus.NOT_FOUND);

			} catch (Exception e) {
				return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		// TODO GET- get personal file
		@Secured("ROLE_STUDENT")
		@JsonView(Views.Private.class)
		@RequestMapping(value = "/getmyfile", method = RequestMethod.GET)
		public ResponseEntity<?> myFile() {
			try {
				StudentEntity se = studentRepository.findByUsername(authenticationFacade.getAuthentication().getName());
				if (!(authenticationFacade.getAuthentication()instanceof AnonymousAuthenticationToken)) {
					logger.info("MyFile -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
						return new ResponseEntity<StudentEntity>(se, HttpStatus.OK);
						}
					else
						return new ResponseEntity<RESTError>(new RESTError(1, "Not allowed"),
								HttpStatus.METHOD_NOT_ALLOWED);	
			} catch (Exception e) {
				return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
						HttpStatus.INTERNAL_SERVER_ERROR);
	}
		}
		// TODO GET- get children files
		@Secured("ROLE_PARENT")
		@JsonView(Views.Private.class)
		@RequestMapping(value = "/getchdfiles", method = RequestMethod.GET)
		public ResponseEntity<?> chdFiles() {
			try {
				ParentEntity pe = parentRepository.findByUsername(authenticationFacade.getAuthentication().getName());
				if (!(authenticationFacade.getAuthentication()instanceof AnonymousAuthenticationToken)) {
					List<StudentEntity>children= pe.getChildren();
					if(pe.getChildren()!=null) {
						logger.info("Children files -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
						return new ResponseEntity<List<StudentEntity>>(children, HttpStatus.OK);
					}	
					else
						return new ResponseEntity<RESTError>(new RESTError(1, "No files to show!"),
								HttpStatus.NOT_FOUND);	
				}else
					return new ResponseEntity<RESTError>(new RESTError(1, "Not allowed"),
							HttpStatus.METHOD_NOT_ALLOWED);	
			} catch (Exception e) {
				return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
	

}

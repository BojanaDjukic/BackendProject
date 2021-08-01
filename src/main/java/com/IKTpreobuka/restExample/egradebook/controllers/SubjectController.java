package com.IKTpreobuka.restExample.egradebook.controllers;

import java.util.Random;
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
import com.IKTpreobuka.restExample.egradebook.entities.SubjectEntity;
import com.IKTpreobuka.restExample.egradebook.repositories.SubjectRepository;
import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.IKTpreobuka.restExample.egradebook.services.SubjectService;
import com.IKTpreobuka.restExample.egradebook.utils.IAuthenticationFacade;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/egradebook/subjects")
public class SubjectController {

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private SubjectService subjectService;
	@Autowired
	private IAuthenticationFacade authenticationFacade;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	void InitBinder(final WebDataBinder binder) {
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}

	// TODO POST- napravi novi predmet
	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@Valid @RequestBody SubjectEntity se, BindingResult result) {
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else {
			if (subjectRepository.findOneByName(se.getName()) != null)
				return new ResponseEntity<RESTError>(new RESTError(1, "Subject already exists!"),
						HttpStatus.NOT_ACCEPTABLE);
			else {
				se.setId((new Random()).nextInt());
				subjectRepository.save(se);
				logger.info("Subject ID: " + se.getId() + " -- CREATED by: " + authenticationFacade.getAuthentication().getName());	
				return new ResponseEntity<SubjectEntity>(se, HttpStatus.CREATED);
			}
		}
	}

	@Secured("ROLE_ADMIN")
	// TODO GET- dobavi sve
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> getAll() throws Exception {
		try {
			logger.info("Subject List -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
			return new ResponseEntity<Iterable<?>>(subjectRepository.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- dobavi jedan predmet- /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getOne(@PathVariable Integer id) {

		try {
			SubjectEntity se = subjectRepository.findOneById(id);
			if (subjectRepository.findOneById(id) != null) {
				logger.info("Subject ID: " + se.getId() + " -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<SubjectEntity>(se, HttpStatus.OK);
			}
			else
				return new ResponseEntity<RESTError>(new RESTError(1, "Subject can not be found"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- izmeni predmet- /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> editSubject(@PathVariable Integer id, @RequestBody SubjectEntity changedSubject) {
		try {
			if (subjectRepository.findOneById(id) != null) {
				if ((subjectRepository.findOneByName(changedSubject.getName()) != null)
						&& (!changedSubject.getName().equals(subjectRepository.findOneById(id).getName()))) //provera da li postoji trazeno ime u bazi a da nije ime predmeta koji se trenutno edituje
					return new ResponseEntity<RESTError>(new RESTError(1, "Subject already exists!"),
							HttpStatus.NOT_ACCEPTABLE);
				else {
					SubjectEntity se = subjectService.editSubject(id, changedSubject);
					logger.info("Subject ID: " + se.getId() +  " -- EDITED by: " + authenticationFacade.getAuthentication().getName());
					return new ResponseEntity<SubjectEntity>(se, HttpStatus.OK);
				}
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "Subject can not be found"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO DELETE - obrisi predmet - /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id) {

		try {
			SubjectEntity se = subjectRepository.findOneById(id);
			if (subjectRepository.findOneById(id) != null) {
				subjectService.deleteSubject(id);
				logger.info("Subject ID: " + se.getId() + " -- DELETED by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<SubjectEntity>(se, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "Subject can not be found"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}

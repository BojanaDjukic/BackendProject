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
import com.IKTpreobuka.restExample.egradebook.entities.UserEntity;
import com.IKTpreobuka.restExample.egradebook.entities.DTO.UserEntityDTO;
import com.IKTpreobuka.restExample.egradebook.repositories.ParentRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.UserRepository;
import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.IKTpreobuka.restExample.egradebook.services.ParentService;
import com.IKTpreobuka.restExample.egradebook.utils.IAuthenticationFacade;
import com.IKTpreobuka.restExample.egradebook.utils.PasswordValidator;
import com.IKTpreobuka.restExample.egradebook.utils.UsernameValidator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/egradebook/users/parents")
public class ParentController extends UserController {

	@Autowired
	private ParentRepository parentRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ParentService parentService;

	@Autowired
	private PasswordValidator passwordValidator;
	@Autowired
	private UsernameValidator usernameValidator;
	
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

	// TODO POST- napravi novog roditelja
	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@Valid @RequestBody UserEntityDTO userDTO, BindingResult result) {
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else { passwordValidator.validate(userDTO, result);
				usernameValidator.validate(userDTO, result);
		}
			ParentEntity pe= parentService.createParent(userDTO);
			logger.info("Parent ID: " + pe.getId() + ", username: " + pe.getUsername() + " -- CREATED by: " + authenticationFacade.getAuthentication().getName());	
			return new ResponseEntity<ParentEntity>(pe, HttpStatus.CREATED);
	}

	// TODO GET- dobavi sve
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	public ResponseEntity<?> getAll() throws Exception {
		try {
			logger.info("Parent List -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
			return new ResponseEntity<Iterable<?>>(parentRepository.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- dobavi jednog roditelja- /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@JsonIgnoreProperties("password")
	public ResponseEntity<?> getOne(@PathVariable Integer id) {

		try {
			ParentEntity pe = parentRepository.findOneById(id);
			if (parentRepository.findOneById(id) != null) {
				logger.info("Parent ID: " + pe.getId() + ", username: " + pe.getUsername() + " -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<UserEntity>(pe, HttpStatus.OK);
			}
			else
				return new ResponseEntity<RESTError>(new RESTError(1, "User can not be found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- izmeni jednog roditelja- /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> editParent(@PathVariable Integer id, @RequestBody ParentEntity changedParent) {
		try {
			if (parentRepository.findOneById(id) != null) {
				if (userRepository.findOneByUsername(changedParent.getUsername())==null) {
				ParentEntity pe = parentService.editParent(id, changedParent);
				logger.info("Parent ID: " + pe.getId() +  " -- EDITED by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<ParentEntity>(pe, HttpStatus.OK);
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

	// TODO DELETE - obrisi roditelja - /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id) {

		try {
			ParentEntity pe = parentRepository.findOneById(id);
			if (parentRepository.findOneById(id) != null) {
			    parentService.deleteParent(id);
				logger.info("PArent ID: " + pe.getId() + ", username: " + pe.getUsername() + " -- DELETED by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<ParentEntity>(pe, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "User not found"), HttpStatus.NOT_FOUND);

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
			List<ParentEntity> parents = parentRepository.findByNameAndSurname(name, surname);
			if (!parentRepository.findByNameAndSurname(name, surname).isEmpty()) {
				logger.info("Parent with name: " + name + "and surname: " + surname + " -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<List<ParentEntity>>(parents, HttpStatus.OK);
			}
			else
				return new ResponseEntity<RESTError>(new RESTError(1, "User can not be found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	


}

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
import com.IKTpreobuka.restExample.egradebook.entities.AdminEntity;
import com.IKTpreobuka.restExample.egradebook.entities.DTO.UserEntityDTO;
import com.IKTpreobuka.restExample.egradebook.repositories.AdminRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.UserRepository;
import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.IKTpreobuka.restExample.egradebook.services.AdminServiceImpl;
import com.IKTpreobuka.restExample.egradebook.utils.IAuthenticationFacade;
import com.IKTpreobuka.restExample.egradebook.utils.PasswordValidator;
import com.IKTpreobuka.restExample.egradebook.utils.UsernameValidator;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/egradebook/users/admins")
public class AdminController {

	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AdminServiceImpl adminService;

	@Autowired
	private PasswordValidator passwordValidator;
	@Autowired
	private UsernameValidator usernameValidator;
	@Autowired
	private IAuthenticationFacade authenticationFacade;

	

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	private final String logs = "C:/Users/bdjuk/workspaceIKTPreobuka/e-gradebook/logs/spring-boot-logging2.log";

	@org.springframework.web.bind.annotation.InitBinder
	protected void InitBinder(final WebDataBinder binder) {
		binder.addValidators(passwordValidator, usernameValidator);
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}

	// TODO POST- napravi novog admina aplikacije

	@JsonView(Views.Admin.class)
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@Secured("ROLE_ADMIN")
	public ResponseEntity<?> createUser(@Valid @RequestBody UserEntityDTO userDTO, BindingResult result) {
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else {
			passwordValidator.validate(userDTO, result);
			usernameValidator.validate(userDTO, result);
		}
		AdminEntity ae = adminService.createAdmin(userDTO);
		logger.info("Admin ID: " + ae.getId() + ", username: " + ae.getUsername() + " -- CREATED by: "
				+ authenticationFacade.getAuthentication().getName());
		return new ResponseEntity<AdminEntity>(ae, HttpStatus.CREATED);
	}

	// TODO GET- dobavi sve
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	public ResponseEntity<?> getAll() {
		try {
			logger.info("Admin List -- FETCHED by: " + authenticationFacade.getAuthentication().getName());
			return new ResponseEntity<Iterable<?>>(adminRepository.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- dobavi jednog admina- /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getOne(@PathVariable Integer id) {

		try {
			AdminEntity ae = adminRepository.findOneById(id);
			if (adminRepository.findOneById(id) != null) {
				logger.info("Admin ID: " + ae.getId() + ", username: " + ae.getUsername() + " -- FETCHED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<AdminEntity>(ae, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "User can not be found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO GET- izmeni jednog admina- /{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> editAdmin(@PathVariable Integer id, @RequestBody AdminEntity changedAdmin) {

		try {
			if (adminRepository.findOneById(id) != null) {
				if (userRepository.findOneByUsername(changedAdmin.getUsername()) == null) { //resiti ako se unese username ovog usera
					AdminEntity ae = adminService.editAdmin(id, changedAdmin);
					logger.info("Admin ID: " + ae.getId() + " -- EDITED by: "
							+ authenticationFacade.getAuthentication().getName());
					return new ResponseEntity<AdminEntity>(ae, HttpStatus.OK);

				} else
					return new ResponseEntity<RESTError>(new RESTError(1, "Username already in use!"),
							HttpStatus.NOT_FOUND);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "User not found"), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO DELETE - obrisi admina - /users/{id}
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id) {

		try {
			AdminEntity ae = adminRepository.findOneById(id);
			if (adminRepository.findOneById(id) != null) {
				adminRepository.deleteById(id);
				logger.info("Admin ID: " + ae.getId() + ", username: " + ae.getUsername() + " -- DELETED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<AdminEntity>(ae, HttpStatus.OK);
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
			List<AdminEntity> admins = adminRepository.findByNameAndSurname(name, surname);
			if (!adminRepository.findByNameAndSurname(name, surname).isEmpty()) {
				logger.info("Admins with name: " + name + "and surname: " + surname + " --- FETCHED by: "
						+ authenticationFacade.getAuthentication().getName());
				return new ResponseEntity<List<AdminEntity>>(admins, HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(1, "User can not be found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError(2, "Internal server error.Error!" + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/download/logs", method = RequestMethod.GET)
	public ResponseEntity<Object> downloadFile() throws IOException {

		String filename = logs;
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.parseMediaType("application/txt")).body(resource);

		return responseEntity;
	}
	

}

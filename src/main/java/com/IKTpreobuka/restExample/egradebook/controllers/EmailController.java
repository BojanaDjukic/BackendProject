package com.IKTpreobuka.restExample.egradebook.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.IKTpreobuka.restExample.egradebook.models.EmailObject;
import com.IKTpreobuka.restExample.egradebook.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(path = "/egradebook")
public class EmailController {

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	private EmailService emailService;

	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.POST, value = "/email/grade")
	public String sendTemplateMessage(@RequestBody EmailObject object) throws Exception {
		if (object == null || object.getTo() == null || object.getText() == null) {
			return null;
		}
		emailService.sendTemplateMessage(null, null, null, null);
		
		logger.info("An email has been sent to : " + object.getTo());
		return "Your mail has been sent!";
	}
}

package com.IKTpreobuka.restExample.egradebook.services;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import com.IKTpreobuka.restExample.egradebook.entities.AdminEntity;
import com.IKTpreobuka.restExample.egradebook.entities.UserRole;
import com.IKTpreobuka.restExample.egradebook.entities.DTO.UserEntityDTO;
import com.IKTpreobuka.restExample.egradebook.repositories.AdminRepository;
import com.IKTpreobuka.restExample.egradebook.utils.Encryption;

@Service
public class AdminServiceImpl implements AdminService{

	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	@Autowired
	private AdminRepository adminRepository;
	
	public AdminEntity createAdmin(UserEntityDTO userDTO) {
		AdminEntity ae = new AdminEntity();
		ae.setId((new Random()).nextInt());
		ae.setName(userDTO.getName());
		ae.setSurname(userDTO.getSurname());
		ae.setEmail(userDTO.getEmail());
		ae.setRole(UserRole.ROLE_ADMIN);
		ae.setUsername(userDTO.getUsername());
		ae.setPassword(Encryption.getPassEncoded(userDTO.getPassword()));
		adminRepository.save(ae);
		return ae;
	}

	public AdminEntity editAdmin(Integer id, AdminEntity changedAdmin) {

		if (adminRepository.findOneById(id) != null) {
			AdminEntity ae = adminRepository.findOneById(id);
			if (changedAdmin.getName() != null) {
				ae.setName(changedAdmin.getName());
				logger.info("Name: " + ae.getName() + " changed to: " + changedAdmin.getName());
				}
			if (changedAdmin.getSurname() != null) {
				ae.setSurname(changedAdmin.getSurname());
				logger.info("Surname: " + ae.getSurname() + " changed to: " + changedAdmin.getSurname());
			}
			if (changedAdmin.getUsername() != null) {
				ae.setUsername(changedAdmin.getUsername());
				logger.info("Username: " + ae.getUsername() + " changed to: " + changedAdmin.getUsername());
			}
			if (changedAdmin.getEmail() != null) {
				ae.setEmail(changedAdmin.getEmail());
				logger.info("Email: " + ae.getEmail() + " changed to: " + changedAdmin.getEmail());
			}
			if (changedAdmin.getPassword() != null) {
				ae.setPassword(changedAdmin.getPassword());
				logger.info("Password: " + ae.getPassword() + " changed to: " + changedAdmin.getPassword());
			}
			if (changedAdmin.getRole() != null)
				ae.setRole(changedAdmin.getRole());
			logger.info("Role: " + ae.getRole() + " changed to: " + changedAdmin.getRole());
			adminRepository.save(ae);
			return ae;

		}
		return null;

}
	}

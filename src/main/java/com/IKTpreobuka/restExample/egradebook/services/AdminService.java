package com.IKTpreobuka.restExample.egradebook.services;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.IKTpreobuka.restExample.egradebook.entities.AdminEntity;
import com.IKTpreobuka.restExample.egradebook.entities.DTO.UserEntityDTO;

public interface AdminService {
	public AdminEntity editAdmin(@PathVariable Integer id, @RequestBody AdminEntity changedAdmin);
	public AdminEntity createAdmin(UserEntityDTO userDTO);
}

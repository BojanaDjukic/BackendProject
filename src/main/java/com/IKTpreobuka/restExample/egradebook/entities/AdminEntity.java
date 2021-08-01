package com.IKTpreobuka.restExample.egradebook.entities;


import javax.persistence.Entity;


import javax.persistence.Table;


@Entity 
@Table(name="Admin")
public class AdminEntity extends UserEntity {
	
	
	public AdminEntity() {
		super();
		
	}

	public AdminEntity(int id,String name,String surname,	String username, String password,String email,UserRole role) {
		super( id, name, surname, username, password, email, role);
		
	}
	
	
}

	
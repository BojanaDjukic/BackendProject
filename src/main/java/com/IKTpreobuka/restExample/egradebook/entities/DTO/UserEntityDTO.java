package com.IKTpreobuka.restExample.egradebook.entities.DTO;


import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class UserEntityDTO {

	
	@NotBlank(message = "First name must be provided")
	@Size(min = 2, max = 15, message = "Name must be between {min} and {max} characters long")
	@Column(name = "Ime", nullable = false)
	@JsonView(Views.Private.class)
	private String name;

	@NotBlank(message = "Last name must be provided")
	@Size(min = 2, max = 15, message = "Surname must be between {min} and {max} characters long")
	@Column(name = "Prezime", nullable = false)
	@JsonView(Views.Private.class)
	private String surname;

	@NotBlank(message = "Username must be provided")
	@Size(min = 5, max = 20, message = "Username must be between {min} and {max} characters long")
	@Column(name = "Korisnicko_ime", nullable = false)
	@JsonView(Views.Private.class)
	private String username;

	@NotBlank(message = "Password must be provided")
	@Size(min = 5, max = 100, message = "Password must be between {min} and {max} characters long")
	@Pattern(regexp =  "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,100}$", message = "Password must be at least 8 characters long and contain a lowercase, an upercase letter and a number")
	@Column(name = "Lozinka", nullable = false)
	@JsonView(Views.Admin.class)
	private String password;
	
	@NotBlank(message = "ConfirmPassword must be provided and match password")
	@JsonView(Views.Admin.class)
	private String confirmPassword;


	@NotBlank(message = "Email  must be provided")
	@Size(min = 5, max = 30, message = "Email must be between {min} and {max} characters long")
	@Email
	@Column(name = "Email", nullable = false)
	@JsonView(Views.Private.class)
	private String email;

	public UserEntityDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	


}

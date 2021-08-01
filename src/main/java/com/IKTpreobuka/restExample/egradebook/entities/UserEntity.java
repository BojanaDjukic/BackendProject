package com.IKTpreobuka.restExample.egradebook.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "Korisnik")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public abstract class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	@JsonView(Views.Admin.class)
	private int id;

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
	@Column(name = "Korisnicko_ime", nullable = false, unique = true)
	@JsonView(Views.Private.class)
	private String username;

	@NotBlank(message = "Password must be provided")
	@JsonIgnore
	@JsonView(Views.Admin.class)
	@Size(min = 5, max = 100, message = "Password must be between {min} and {max} characters long")
	@Pattern(regexp =  "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,100}$", message = "Password must be at least 8 characters long and contain a lowercase, an upercase letter and a number")
	@Column(name = "Lozinka", nullable = false)
	//"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,100}$"
	private String password;

	@NotBlank(message = "Email  must be provided")
	@Size(min = 5, max = 30, message = "Email must be between {min} and {max} characters long")
	@Email
	@Column(name = "Email", nullable = false)
	@JsonView(Views.Private.class)
	private String email;

	@JsonView(Views.Admin.class)
	@Column(name = "Uloga")
	private UserRole role;

	@Version
	@Column(name = "Verzija")
	private Integer version;

	public UserEntity() {
		super();
	}

	public UserEntity(int id, String name, String surname, String username, String password,
			String email, UserRole role) {
		super();
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}
	


}

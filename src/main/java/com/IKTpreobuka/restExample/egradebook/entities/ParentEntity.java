package com.IKTpreobuka.restExample.egradebook.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "Roditelj")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ParentEntity extends UserEntity {

	@OneToMany(mappedBy = "parent", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonManagedReference(value = "parentStudent")
	@JsonView(Views.Private.class)
	@Column(name = "Roditelj")
	private List<StudentEntity> children;

	public ParentEntity() {
		super();

	}

	public ParentEntity(int id, String name, String surname, String username, String password, 
			@Email String email, UserRole role, List<StudentEntity> children) {
		super(id, name, surname, username, password, email, role);

		this.children = children;

	}

	public List<StudentEntity> getChildren() {
		return children;
	}

	public void setChildren(List<StudentEntity> children) {
		this.children = children;
	}

}

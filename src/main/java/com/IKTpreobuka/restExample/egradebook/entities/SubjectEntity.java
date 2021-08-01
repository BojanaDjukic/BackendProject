package com.IKTpreobuka.restExample.egradebook.entities;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table (name= "Predmet")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SubjectEntity {
	
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	@Column(name= "Id")
	@JsonView(Views.Admin.class)
	private int id;
	
	@Column(name = "Naziv",unique=true)
	@JsonView(Views.Private.class)
	@NotBlank (message= "Name must be assigned")
	private String name;
	
	@Column(name= "fond",nullable = false)
	@JsonView(Views.Private.class)
	@NotNull(message= "Class hours must be set")
	@Min(value=1, message = "Hours allowed 1 and more.")
	private Integer hours;
	
	@OneToMany(mappedBy = "subject", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonManagedReference(value = "subjectAssigned")
	@JsonView(Views.Private.class)
	@Column(name = "Predmet")
    private List<AssignedEntity> assigned;
    
	public SubjectEntity() {
		super();
	}

	public SubjectEntity(int id, String name) {
		super();
		this.id = id;
		this.name = name;
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
	
	public Integer getHours() {
		return hours;
	}

	public void setHours(Integer hours) {
		this.hours = hours;
	}

	public List<AssignedEntity> getAssigned() {
		return assigned;
	}

	public void setAssigned(List<AssignedEntity> assigned) {
		this.assigned = assigned;
	}

	
}

package com.IKTpreobuka.restExample.egradebook.entities;


import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;

import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;


@Entity
@Table (name= "Odeljenje")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SchoolClass {


	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	@Column(name= "Id")
	@JsonView(Views.Admin.class)
	private int id;

	@Column(name= "oznaka")
	@JsonView(Views.Private.class)
	@NotBlank (message = "Name must be provided")
	private String name;
	
	@Column(name= "Razred", nullable=false)
	@JsonView(Views.Private.class)
	private SchoolYear year;
	
	@JsonView(Views.Private.class)
	@OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "Razredni")
	private TeacherEntity classElder;
	
	@OneToMany(mappedBy = "schoolClass", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonManagedReference(value = "studentClass")
	@JsonView(Views.Teacher.class)
	@Column(name = "Odeljenje")
	private List<StudentEntity> students;
	

	@Version
	@Column(name="Verzija")
	private Integer version;

	public SchoolClass() {
		super();
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SchoolYear getYear() {
		return year;
	}

	public void setYear(SchoolYear year) {
		this.year = year;
	}


	public TeacherEntity getClassElder() {
		return classElder;
	}

	public void setClassElder(TeacherEntity classElder) {
		this.classElder = classElder;
	}

	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<StudentEntity> getStudents() {
		return students;
	}

	public void setStudents(List<StudentEntity> students) {
		this.students = students;
	}


}

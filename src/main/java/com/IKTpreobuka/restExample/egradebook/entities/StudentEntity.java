package com.IKTpreobuka.restExample.egradebook.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table (name= "Ucenik")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StudentEntity extends UserEntity {
	
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonBackReference (value="parentStudent")
	@JsonView(Views.Private.class)
	@JoinColumn(name = "Roditelj" )
	private ParentEntity parent;
	
	@OneToMany(mappedBy = "student", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonManagedReference(value = "fileStudent")
	@JsonView(Views.Private.class)
	@Column(name = "Ucenik")
	private List<StudentFileEntity> files;
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonBackReference (value="studentClass")
	@JoinColumn(name = "Odeljenje" )
	@JsonView(Views.Private.class)
	private SchoolClass schoolClass;
	
	
	public SchoolClass getSchoolClass() {
		return schoolClass;
	}

	public void setSchoolClass(SchoolClass schoolClass) {
		this.schoolClass = schoolClass;
	}

	public StudentEntity() {
		super();
	
	}

	public ParentEntity getParent() {
		return parent;
	}

	public void setParent(ParentEntity parent) {
		this.parent = parent;
	}

	public List<StudentFileEntity> getFiles() {
		return files;
	}

	public void setFiles(List<StudentFileEntity> files) {
		this.files = files;
	}

	}
	


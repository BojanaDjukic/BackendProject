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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;


@Entity
@Table(name = "karton_ucenika")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class StudentFileEntity {
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	@JsonView(Views.Public.class)
	private Integer id;
	

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonBackReference (value="fileStudent")
	@JoinColumn(name = "Ucenik" )
	@JsonView(Views.Private.class)
	private StudentEntity student;
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonBackReference (value="assignedFile")
	@JoinColumn(name = "assigned" )
	@JsonView(Views.Private.class)
	private AssignedEntity assigned;
	
	@OneToMany(mappedBy = "studentFile", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonManagedReference(value = "gradeFile")
	@JsonView(Views.Private.class)
	@Column(name = "Karton_ucenika")
	private List<GradeEntity> grades;
	

	public StudentFileEntity() {
		super();
	}
	
	public StudentFileEntity(Integer id, StudentEntity student, List<GradeEntity> grades,
			SubjectEntity subject) {
		super();
		this.id = id;
		this.student = student;
		this.grades = grades;
	}


	public AssignedEntity getAssigned() {
		return assigned;
	}

	public void setAssigned(AssignedEntity assigned) {
		this.assigned = assigned;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public StudentEntity getStudent() {
		return student;
	}


	public void setStudent(StudentEntity student) {
		this.student = student;
	}

	
	public List<GradeEntity> getGrades() {
		return grades;
	}


	public void setGrades(List<GradeEntity> grades) {
		this.grades = grades;
	}
  
}

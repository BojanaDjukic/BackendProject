package com.IKTpreobuka.restExample.egradebook.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "ocena")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class GradeEntity {

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	@JsonView(Views.Admin.class)
	private Integer id;
	
	@Column(name= "Ocena", nullable=false)
	@JsonView(Views.Private.class)
	@Min(value=1, message = "1-5 allowed")
	@Max(value=5, message = "1-5 allowed")
	private Integer value;
	
	@Column(name= "Datum", nullable=false)
	@JsonView(Views.Private.class)
	@JsonFormat(shape=JsonFormat.Shape.STRING,pattern="dd-MM-yyyy")
	private Date date;
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonBackReference (value="gradeFile")
	@JoinColumn(name = "Karton_ucenika" )
	//@NotBlank(message = "First name must be provided")
	@JsonView(Views.Private.class)
	private StudentFileEntity studentFile;

	public GradeEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public StudentFileEntity getStudentFile() {
		return studentFile;
	}

	public void setStudentFile(StudentFileEntity studentFile) {
		this.studentFile = studentFile;
	}
	
}

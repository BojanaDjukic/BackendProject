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
import javax.validation.constraints.NotNull;

import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table (name= "Predaje")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AssignedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	@JsonView(Views.Admin.class)
	private Integer id;
	
	@NotNull(message = "Semester must be set")
	@JsonView(Views.Private.class)
	private Semester semester;
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonBackReference (value="teacherAssigned")
	@JoinColumn(name = "Ucitelj" )
	@JsonView(Views.Private.class)
	private TeacherEntity teacher;
	
	
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonBackReference (value="subjectAssigned")
	@JoinColumn(name = "Predmet" )
	@JsonView(Views.Private.class)
	private SubjectEntity subject;
	
	

	@OneToMany(mappedBy = "assigned", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonManagedReference(value = "assignedFile")
	@JsonView(Views.Private.class)
	@Column(name = "assigned")
	private List<StudentFileEntity> files;

	public AssignedEntity() {
		super();
	
	}

	public AssignedEntity(Integer id, Semester semester, TeacherEntity teacher, SubjectEntity subject,
			List<StudentFileEntity> files) {
		super();
		this.id = id;
		this.semester = semester;
		this.teacher = teacher;
		this.subject = subject;
		this.files = files;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Semester getSemester() {
		return semester;
	}

	public void setSemester(Semester semester) {
		this.semester = semester;
	}

	public TeacherEntity getTeacher() {
		return teacher;
	}

	public void setTeacher(TeacherEntity teacher) {
		this.teacher = teacher;
	}

	public SubjectEntity getSubject() {
		return subject;
	}

	public void setSubject(SubjectEntity subject) {
		this.subject = subject;
	}

	public List<StudentFileEntity> getFiles() {
		return files;
	}

	public void setFiles(List<StudentFileEntity> files) {
		this.files = files;
	}

	
}

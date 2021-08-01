package com.IKTpreobuka.restExample.egradebook.entities;


import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.IKTpreobuka.restExample.egradebook.security.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table (name= "Ucitelj")
@Getter
@Setter
@NoArgsConstructor
public class TeacherEntity extends UserEntity {
	
	
	@JsonView(Views.Private.class)
	@OneToOne(mappedBy = "classElder", cascade = CascadeType.REFRESH,fetch = FetchType.LAZY)
	@JsonBackReference
	private SchoolClass elderClass;

	@OneToMany(mappedBy = "teacher", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonManagedReference(value = "teacherAssigned")
	@JsonView(Views.Private.class)
	@Column(name = "Ucitelj")
	private List<AssignedEntity> assigned;
	
	public TeacherEntity() {
		super();
		
	}
	
	public TeacherEntity(int id, String name,String surname,String username,String password,String email,UserRole role, SchoolClass elderClass, Set<SchoolClass> classes) {
		super(id, name, surname, username, password, email, role);
		this.elderClass=elderClass;
	}


	public List<AssignedEntity> getAssigned() {
		return assigned;
	}


	public void setAssigned(List<AssignedEntity> assigned) {
		this.assigned = assigned;
	}
	public SchoolClass getElderClass() {
		return elderClass;
	}

	public void setElderClass(SchoolClass elderClass) {
		this.elderClass = elderClass;
	}

	
}

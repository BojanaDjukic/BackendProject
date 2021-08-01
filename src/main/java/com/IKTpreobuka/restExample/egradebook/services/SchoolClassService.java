package com.IKTpreobuka.restExample.egradebook.services;

import com.IKTpreobuka.restExample.egradebook.entities.SchoolClass;

public interface SchoolClassService {

	
	public SchoolClass elderToSC(Integer teacherId,Integer scId);
	public SchoolClass studentToSC(Integer studentId,Integer scId);
	public SchoolClass createClass(SchoolClass sc);
	public SchoolClass editClass(Integer id, SchoolClass sc);
	public SchoolClass setYear(SchoolClass sc);
	public SchoolClass deleteClass(Integer id);
	
}

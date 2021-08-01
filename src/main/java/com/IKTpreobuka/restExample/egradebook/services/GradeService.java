package com.IKTpreobuka.restExample.egradebook.services;

import com.IKTpreobuka.restExample.egradebook.entities.GradeEntity;

public interface GradeService {
	public GradeEntity grade(GradeEntity ge, Integer studentId, Integer subjectId)throws Exception;
	public GradeEntity editGrade(Integer gradeId, GradeEntity changedGrade);
	public GradeEntity gradeForAdmin(GradeEntity ge, Integer studentId, Integer subjectId, Integer teacherId) throws Exception; 

}

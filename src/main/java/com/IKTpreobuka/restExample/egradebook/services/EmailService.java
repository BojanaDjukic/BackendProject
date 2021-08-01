package com.IKTpreobuka.restExample.egradebook.services;

import com.IKTpreobuka.restExample.egradebook.entities.GradeEntity;

public interface EmailService {

	public void sendTemplateMessage(Integer studentId,Integer subjectId, Integer teacherId, GradeEntity ge) throws Exception;
}

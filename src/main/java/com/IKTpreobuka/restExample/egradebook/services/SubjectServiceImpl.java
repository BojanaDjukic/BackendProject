package com.IKTpreobuka.restExample.egradebook.services;

import java.util.List;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.IKTpreobuka.restExample.egradebook.entities.AssignedEntity;
import com.IKTpreobuka.restExample.egradebook.entities.SubjectEntity;
import com.IKTpreobuka.restExample.egradebook.repositories.SubjectRepository;

@Service
public class SubjectServiceImpl implements SubjectService {

	@Autowired
	private SubjectRepository subjectRepository;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	public SubjectEntity editSubject(Integer id,SubjectEntity changedSubject) {

		if (subjectRepository.findOneById(id) != null) {
			SubjectEntity se = subjectRepository.findOneById(id);
			if (changedSubject.getName() != null) {
				se.setName(changedSubject.getName());
				logger.info("Name: " + se.getName() + " changed to: " + changedSubject.getName());
			}
			if(changedSubject.getHours()!= null) {
				se.setHours(changedSubject.getHours());
				logger.info("Hours: " + se.getHours() + " changed to: " + changedSubject.getHours());
			}
			return se;
		}
		return null;

	}
	
	public SubjectEntity deleteSubject(Integer id) {
		SubjectEntity se=subjectRepository.findOneById(id);
		List<AssignedEntity> assigned=se.getAssigned();
		for(AssignedEntity a: assigned)
		a.setSubject(null);
		Hibernate.initialize(se.getAssigned());
		subjectRepository.deleteById(id);
		return se;
	}
}

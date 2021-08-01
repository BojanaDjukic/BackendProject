package com.IKTpreobuka.restExample.egradebook.services;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.IKTpreobuka.restExample.egradebook.entities.GradeEntity;
import com.IKTpreobuka.restExample.egradebook.repositories.StudentRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.SubjectRepository;
import com.IKTpreobuka.restExample.egradebook.repositories.TeacherRepository;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	public JavaMailSender emailSender;
	@Autowired
	public StudentRepository studentRepository;
	@Autowired
	public TeacherRepository teacherRepository;
	@Autowired
	public SubjectRepository subjectRepository;
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Override
	public void sendTemplateMessage(Integer studentId,Integer subjectId, Integer teacherId, GradeEntity ge) throws Exception {
		MimeMessage mail = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mail, true);
		helper.setTo(studentRepository.findOneById(studentId).getParent().getEmail());
		helper.setSubject("Nova ocena");
		String text = "Poštovani/a, "  + "\r\n\r\n" + "     Vašem detetu "
				+ studentRepository.findOneById(studentId).getName() + "  je upravo dodeljena nova ocena " + ge.getValue() + 
				", iz predmeta " + subjectRepository.findOneById(subjectId).getName() + ", od strane učitelja " + teacherRepository.findOneById(teacherId).getSurname()+
				" " + teacherRepository.findOneById(teacherId).getName() + "\r\n\r\n" + "Srdačan pozdrav, " + "\r\n\r\n" + "Vaša škola!";
		helper.setText(text, true);
		emailSender.send(mail);
		logger.info("newGrade email sent to: " + studentRepository.findOneById(studentId).getParent().getEmail());

}
}

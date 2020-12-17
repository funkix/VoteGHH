package com.gesthelp.vote.service;

import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Service
@Log
public class EmailSender {

	private final static String NOREPLY_FROM = "noreply@ghh.fr";
	@Autowired
	private JavaMailSender emailSender;

	public void sendMessage(String to, String subject, String text) {
		log.info("sendSimpleMessage sending to " + to + " subject=" + subject);
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(NOREPLY_FROM);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		try {
			emailSender.send(message);
			log.info("sendSimpleMessage sent to " + to + " subject=" + subject);
		} catch (MailException e) {
			log.log(Level.SEVERE, "sendSimpleMessage envoir mail impossible", e);
			throw e;
		}
	}
}

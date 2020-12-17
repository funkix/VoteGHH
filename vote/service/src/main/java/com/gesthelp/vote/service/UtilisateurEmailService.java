package com.gesthelp.vote.service;

import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import com.gesthelp.vote.domain.Utilisateur;

import lombok.extern.java.Log;

@Service
@Log
public class UtilisateurEmailService {

	@Autowired
	private EmailSender emailSender;

	// " a voté ":

	@Value("${gesthelp.mail.voteconfirm.subject}")
	private String aVoteSubject;
	@Value("${gesthelp.mail.voteconfirm.message}")
	private String aVoteMessage;

	public void sendMessageAVote(Utilisateur user, String hash) {
		log.info("sendMessageAVote sending to " + user.getMail() + ", hash=" + hash);
		try {
			String msg = this.aVoteMessage.replace("@hash@", hash);
			emailSender.sendMessage(user.getMail(), aVoteSubject, msg);
			log.info("sendMessageAVote sent to " + user.getMail());
		} catch (MailException e) {
			log.log(Level.SEVERE, "sendSimpleMessage envoir mail impossible", e);
			throw e;
		}
	}
}

package com.gesthelp;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.gesthelp.vote.service.EmailSender;

@SpringBootApplication
@ComponentScan("com.gesthelp.vote")
@EnableBatchProcessing
public class EmailSendApp implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.exit(SpringApplication.run(EmailSendApp.class, args));
	}

	@Override
	public void run(String... args) throws Exception {
		sendMessage("mir.richard@gmail.com", "noreply@test.fr", "test subject", "test text");
	}

	@Autowired
	private EmailSender emailSender;

	public void sendMessage(String to, String from, String subject, String text) {
		System.out.println("sendSimpleMessage sending to " + to + " subject=" + subject);
		emailSender.sendMessage(to, subject, text);
	}
}

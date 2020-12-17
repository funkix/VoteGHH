package com.gesthelp.vote.batch.mail.voteconfirm;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gesthelp.vote.batch.internal.AbstractJobConfig;

@Configuration
public class SendVoteMailConfig extends AbstractJobConfig {
	@Bean
	public Tasklet sendMail() {
		return new SendEmailTasklet();
	}

	@Bean
	public Step sendMailStep() {
		return stepBuilderFactory.get("sendMailStep").tasklet(sendMail()).build();
	}

	@Bean
	public Job sendVoteConfirmMailJob() {
		return jobBuilderFactory.get("sendVoteConfirmMailJob").start(sendMailStep()).listener(durationListener).build();
	}

}

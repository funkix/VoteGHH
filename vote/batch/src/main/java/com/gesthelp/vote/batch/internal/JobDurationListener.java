package com.gesthelp.vote.batch.internal;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JobDurationListener implements JobExecutionListener {
	private Long start, end;

	@BeforeJob
	public void beforeJob(JobExecution execution) {
		start = System.currentTimeMillis();
	}

	@AfterJob
	public void afterJob(JobExecution execution) {
		end = System.currentTimeMillis();
		log.info("Job duration: " + (end - start) / 1000 + " sec");
	}

}

package com.gesthelp.vote.batch.launcher;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;

import com.gesthelp.vote.batch.dummy.Commons;
import com.gesthelp.vote.batch.internal.JobDurationListener;

public class BaseLauncher implements Commons {
	@Autowired
	protected JobBuilderFactory jobBuilderFactory;
	@Autowired
	protected StepBuilderFactory stepBuilderFactory;
	@Autowired
	protected JobDurationListener durationListener;
	@Autowired
	protected DataSource dataSource;

	protected JobExecution launch(Job job, JobParametersBuilder builder, Boolean async) {
		builder.addString("nowInMillis", "" + System.currentTimeMillis());
		// log.info("JobParameters : " + builder.toJobParameters().toString());
		SimpleJobLauncher launcher = new SimpleJobLauncher();

		if (async) {
			launcher.setTaskExecutor(new SimpleAsyncTaskExecutor());
		} else {
			launcher.setTaskExecutor(new SyncTaskExecutor());
		}
		JobExecution execution;
		try {
			execution = launcher.run(job, builder.toJobParameters());
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
			throw new RuntimeException("oups", e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("oups", e);
		}
		return execution;
	}
}

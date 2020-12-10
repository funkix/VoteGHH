package com.gesthelp.vote.batch.dummy;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gesthelp.vote.batch.internal.AbstractJobConfig;

@Configuration
public class DummyJobConfig extends AbstractJobConfig {

	@Bean
	public Tasklet dummyTasklet() {
		return new DummyTasklet();
	}

	@Bean
	public Step dummyTaskletStep() {
		return stepBuilderFactory.get("dummyStep").tasklet(dummyTasklet()).build();
	}

	@Bean
	public Job gesthelpVoteDummyJob() {
		return jobBuilderFactory.get("gesthelpVoteDummyJob").validator(validator()).start(dummyTaskletStep()).listener(durationListener).build();
	}

	@Bean
	public JobParametersValidator validator() {
		return parameters -> {
			if (parameters.getString("my_job_parameter_name", null) == null) {
				throw new JobParametersInvalidException("my_job_parameter_name parameter is not found.");
			}
		};
	}

}

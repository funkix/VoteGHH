package com.gesthelp.vote.batch.dummy;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import lombok.extern.java.Log;

@Log
public class DummyTasklet implements Tasklet, StepExecutionListener {
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.info("DummyTasklet 'execute' executed");
		return RepeatStatus.FINISHED;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		log.info("DummyTasklet 'execute' before");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		log.info("DummyTasklet 'execute' after");
		return ExitStatus.COMPLETED;
	}

}

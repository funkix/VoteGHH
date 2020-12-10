package com.gesthelp;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import lombok.extern.java.Log;

@Log
@SpringBootApplication
@ComponentScan("com.gesthelp.vote")
@EnableBatchProcessing
@Parameters(separators = "=")

public class JobLauncherApp implements CommandLineRunner {

	@Parameter(names = { "-j", "--job" }, description = "nom du job", required = true, order = 0)
	private String jobName;

	@Parameter(names = { "-p", "--parameter" }, description = "parametre nom_param=value[#l]", required = false, order = 1)
	private List<String> param;

	@Autowired
	private ApplicationContext context;
	@Autowired
	private JobLauncher jobLauncher;

	public static void main(String[] args) {
		println(args);
		(new JobLauncherApp()).parseArgs(args);
		SpringApplication.exit(SpringApplication.run(JobLauncherApp.class, args));
	}

	private static void println(String[] args) {
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				System.out.println("main args[" + i + "]=" + args[i]);
			}
		}
	}

	private void parseArgs(String[] args) {
		JCommander commander = JCommander.newBuilder().addObject(this).acceptUnknownOptions(true).build();
		try {
			commander.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			commander.usage();
			System.exit(1);
		}
		System.out.println("jobName=" + this.jobName);
		if (this.param != null) {
			for (String p : this.param) {
				String[] keyvalType = keyValType(p);
				System.out.println("param key=" + keyvalType[0] + ", val=" + keyvalType[1] + ", type=" + keyvalType[2]);
			}
		}
	}

	private String[] keyValType(String p) {
		String[] keyval = p.split("=");
		String key = keyval[0];
		String val = keyval[1];
		String type = "s";
		if (val.indexOf("#") != -1) {
			String[] valtype = val.split("#");
			val = valtype[0];
			type = valtype[1];
		}
		return new String[] { key, val, type };
	}

	@Override
	public void run(String... arg0) throws Exception {
		log.info("*********** runs");
		this.parseArgs(arg0);
		Job job = null;
		JobParametersBuilder pBuilder = new JobParametersBuilder();
		pBuilder.addLong("time", System.currentTimeMillis());
		job = (Job) context.getBean(this.jobName);

		if (this.param != null) {
			for (String p : this.param) {
				String[] keyvalType = keyValType(p);
				log.info(job.getName() + " parameter :" + keyvalType[0] + "=" + ", val=" + keyvalType[1] + ", type=" + keyvalType[2]);
				if ("s".equalsIgnoreCase(keyvalType[2])) {
					pBuilder.addString(keyvalType[0], keyvalType[1]);
				} else if ("l".equalsIgnoreCase(keyvalType[2])) {
					pBuilder.addLong(keyvalType[0], new Long(keyvalType[1]));
				}
			}
		}
		log.info("*********** launching job with params: " + pBuilder.toJobParameters());
		JobExecution jobExecution = jobLauncher.run(job, pBuilder.toJobParameters());
		JobInstance jobInstance = jobExecution.getJobInstance();
		log.info(String.format("*********** job instance Id: %d", jobInstance.getId()));
	}

}

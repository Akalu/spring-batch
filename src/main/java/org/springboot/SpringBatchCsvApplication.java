package org.springboot;

import javax.annotation.PreDestroy;
import org.springboot.batch.JobFactory;
import org.springboot.config.ShutdownManager;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@Slf4j
public class SpringBatchCsvApplication implements CommandLineRunner {

	@Autowired
	@Qualifier("myJobLauncher")
	private JobLauncher jobLauncher;

	@Autowired
	JobFactory jobFactory;

	static ConfigurableApplicationContext applicationContext;

	public static void main(String[] args) throws InterruptedException {
		log.info("STARTING THE APPLICATION");
		applicationContext = SpringApplication.run(SpringBatchCsvApplication.class, args);
		log.info("APPLICATION FINISHED");
		ShutdownManager sm = new ShutdownManager();
		sm.setAppContext(applicationContext);
		sm.initiateShutdown(0);
	}

	@PreDestroy
	public void onDestroy() throws Exception {
		log.info("gracefull shutdown");// added due to https://github.com/spring-projects/spring-batch/issues/3725
	}

	@Override
	public void run(String... args) throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		log.info("EXECUTING : command line runner");
		Job job = jobFactory.csvFileToDatabaseJob();
		JobParameters jobParameters = new JobParametersBuilder().addString("name", "job " + 0).toJobParameters();
		JobExecution res = jobLauncher.run(job, jobParameters);
		log.info("{}", job.getName());
		log.info("{}", res.getJobId());
	}

}

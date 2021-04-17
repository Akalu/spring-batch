package org.springboot.config;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableBatchProcessing(modular = true)
@Slf4j
public class BatchConfig extends DefaultBatchConfigurer {

	@Bean
	@Primary
	public TaskExecutor threadPoolTaskExecutor() {

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setMaxPoolSize(12);
		executor.setCorePoolSize(8);
		executor.setQueueCapacity(15);
		executor.setAwaitTerminationSeconds(5);

		return executor;
	}

	@Override
	@Bean(name = "myJobLauncher")
	public JobLauncher getJobLauncher() {
		try {
			SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
			jobLauncher.setJobRepository(getJobRepository());
			jobLauncher.setTaskExecutor(threadPoolTaskExecutor());
			jobLauncher.afterPropertiesSet();
			return jobLauncher;

		} catch (Exception e) {
			log.error("Can't load SimpleJobLauncher with SimpleAsyncTaskExecutor: {} fallback on default", e);
			return super.getJobLauncher();
		}
	}
}
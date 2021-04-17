package org.springboot.batch;

import org.springboot.batch.listener.JobCompletionNotificationListener;
import org.springboot.data.MovieInfo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JobFactory {
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	ItemProcessor<MovieInfo, MovieInfo> processor;
	
	@Autowired
	JdbcBatchItemWriter<MovieInfo> writer;
	
	@Autowired
	FlatFileItemReader<MovieInfo> reader;

	@Autowired
	private JobCompletionNotificationListener listener;



	Step csvFileToDatabaseStep() {
		return stepBuilderFactory.get("csvFileToDatabaseStep").<MovieInfo, MovieInfo>chunk(1)
				.reader(reader)
				.faultTolerant()
				.skipPolicy(new DataDuplicateSkipper())
				.processor(processor)
				.writer(writer)
				.build();
	}

	public Job csvFileToDatabaseJob() {
		return jobBuilderFactory.get("csvFileToDatabaseJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(csvFileToDatabaseStep())
				.end()
				.build();
	}


}

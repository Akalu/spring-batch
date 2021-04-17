package org.springboot.batch;

import javax.sql.DataSource;

import org.springboot.batch.listener.JobCompletionNotificationListener;
import org.springboot.data.MovieInfo;
import org.springboot.processor.MovieProcessor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class BatchIO {
	
	@Autowired
	public DataSource dataSource;

	@Autowired
	JobCompletionNotificationListener listener;



	@Bean
	public FlatFileItemReader<MovieInfo> csvReader() {
		FlatFileItemReader<MovieInfo> reader = new FlatFileItemReader<>();
		
		reader.setResource(new ClassPathResource("movies.csv"));
		
		reader.setLineMapper(new DefaultLineMapper<MovieInfo>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "id", "title", "description" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<MovieInfo>() {
					{
						setTargetType(MovieInfo.class);
					}
				});
			}
		});
		return reader;
	}

	@Bean
	ItemProcessor<MovieInfo, MovieInfo> movieProcessor() {
		return new MovieProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<MovieInfo> csvWriter() {
		JdbcBatchItemWriter<MovieInfo> writer = new JdbcBatchItemWriter<>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		writer.setSql("INSERT INTO movies (id, title, description) VALUES (:id, :title, :description)");
		writer.setDataSource(dataSource);
		return writer;
	}

}

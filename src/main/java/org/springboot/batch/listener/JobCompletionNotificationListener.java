package org.springboot.batch.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springboot.config.ShutdownBatchHook;
import org.springboot.data.MovieInfo;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
  @Autowired
  public DataSource dataSource;


	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("============ JOB FINISHED ============");
			log.info("Verifying the results...");

			List<MovieInfo> results = jdbcTemplate.query("SELECT id, title, description FROM movies",
					new RowMapper<MovieInfo>() {
						@Override
						public MovieInfo mapRow(ResultSet rs, int row) throws SQLException {
							return new MovieInfo(rs.getString(1), rs.getString(2), rs.getString(3));
						}
					});

			for (MovieInfo movieRecord : results) {
				log.info("Found {} in the db", movieRecord);
			}
			ShutdownBatchHook.get().upsertJob(jobExecution.getJobId(), jobExecution.getStatus());
		}
	}

	@Override
	public void beforeJob(final JobExecution jobExecution) {
	}

}

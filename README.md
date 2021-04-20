About
======

This is a simple project implementing asynchronous data processing on the basis of Spring Batch.

Overview
=========

We are creating a job to perform reading line by line csv file, preparing insert statement and executing it on the database.

The whole workflow is defined in two files: BatchIO and JobFactory.

Here is a brief description of some common classes:

1. JobBuilderFactory(JobRepository jobRepository)  - a convenient factory for a JobBuilder to build a job

2. StepBuilderFactory which sets the JobRepository - a convenient factory to build steps.

A Step is a domain object that contains an independent, sequential phase of a batch job and contains all of the information needed to define and control the actual batch processing. 


Now that we’ve created the reader and processor for data we need to write it. For the reading, we’ve been using chunk-oriented processing, meaning we’ve been reading the data one at a time.

3. FlatFileItemReader<T> ItemReader that reads lines from input setResource(Resource) In this project we are using CSV file.

FlatFileItemReader can be parameterized:
* setLineMapper method converts Strings to objects representing the item.
* setResource. Public setter for the input resource.
* setLinesToSkip the number of lines to skip at the start of a file.

4. Defining the Datasource DataSource dataSource().

Here we can define the type of datasource i.e (MySql, Oracle etc) and scripts specific to the datasource are defined. For Spring Boot it is enough to provide a dependency in pom file and schema.

5. The JdbcBatchItemWriter.

Used to integrate the datasource and the itemWriter object - it will set JDBC connection and the sql statement to execute in the database.


Notes
======

The most tricky moment is tied with correct shutdown of Spring Batch job schedulers. The issue is - when other JDBC sources are used along with JPA this results in live threads after all jobs have completed their work.

As for Spring Boot 2.4.x it is still the unresolved issue.

In this project the problem is solved through introduction of ShutdownBatchHook singleton class which is used to track the state of all jobs and to invoke explicitly close() method on ConfigurableApplicationContext. 


Building project
=================

```
mvn clean package
``` 

Run as the standard Spring Boot app:

```
java -jar spring-batch-csv-0.0.1.jar
```

Observe the output as the following one:

```
34.515  INFO 4024 --- [           main] o.springboot.SpringBatchCsvApplication   : Starting SpringBatchCsvApplication v0.0.1 using Java 14.0.1
34.515  INFO 4024 --- [           main] o.springboot.SpringBatchCsvApplication   : No active profile set, falling back to default profiles: default
35.228  INFO 4024 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
35.244  INFO 4024 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 10 ms. Found 0 JPA repository interfaces.
35.653  INFO 4024 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
36.048  INFO 4024 --- [           main] com.zaxxer.hikari.pool.PoolBase          : HikariPool-1 - Driver does not support get/set network timeout for connections. (feature not supported)
36.048  INFO 4024 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
36.190  INFO 4024 --- [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
36.299  INFO 4024 --- [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 5.4.25.Final
36.534  INFO 4024 --- [           main] o.hibernate.annotations.common.Version   : HCANN000001: Hibernate Commons Annotations {5.1.2.Final}
36.705  INFO 4024 --- [           main] org.hibernate.dialect.Dialect            : HHH000400: Using dialect: org.hibernate.dialect.HSQLDialect
36.943  INFO 4024 --- [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
36.958  INFO 4024 --- [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
36.974  WARN 4024 --- [           main] o.s.b.c.c.a.DefaultBatchConfigurer       : No transaction manager was provided, using a DataSourceTransactionManager
36.990  INFO 4024 --- [           main] o.s.b.c.r.s.JobRepositoryFactoryBean     : No database type set, using meta data indicating: HSQL
37.194  INFO 4024 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : No TaskExecutor has been set, defaulting to synchronous executor.
37.225  INFO 4024 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'threadPoolTaskExecutor'
37.429  INFO 4024 --- [           main] o.s.s.c.ThreadPoolTaskScheduler          : Initializing ExecutorService 'taskScheduler'
37.475  INFO 4024 --- [           main] o.springboot.SpringBatchCsvApplication   : Started SpringBatchCsvApplication in 3.416 seconds (JVM running for 3.907)
37.475  INFO 4024 --- [           main] o.s.b.a.b.JobLauncherApplicationRunner   : Running default command line with: []
37.475  INFO 4024 --- [           main] o.springboot.SpringBatchCsvApplication   : EXECUTING : command line runner
37.615  INFO 4024 --- [           main] o.springboot.SpringBatchCsvApplication   : csvFileToDatabaseJob
37.615  INFO 4024 --- [           main] o.springboot.SpringBatchCsvApplication   : 0
37.615  INFO 4024 --- [lTaskExecutor-1] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=csvFileToDatabaseJob]] launched with the following parameters: [{name=job 0}]
37.615  INFO 4024 --- [           main] o.springboot.SpringBatchCsvApplication   : APPLICATION FINISHED
37.615  INFO 4024 --- [           main] org.springboot.config.ShutdownBatchHook  : initiate wait loop
37.662  INFO 4024 --- [lTaskExecutor-1] o.s.batch.core.job.SimpleStepHandler     : Executing step: [csvFileToDatabaseStep]
37.709  INFO 4024 --- [lTaskExecutor-1] org.springboot.processor.MovieProcessor  : Converting MovieInfo [id=1, title=Cowboy Bebop, description=In the year 2071, humanity has colonized ...] -> MovieInfo [id=1, title=Cowboy Bebop, description=In the year 2071, humanity has colonized ...]
37.724  INFO 4024 --- [lTaskExecutor-1] org.springboot.processor.MovieProcessor  : Converting MovieInfo [id=1000, title=Uchuu Kaizoku Captain Harlock, description=The year is 2977. Mankind has become com ...] -> MovieInfo [id=1000, title=Uchuu Kaizoku Captain Harlock, description=The year is 2977. Mankind has become com ...]
37.724  INFO 4024 --- [lTaskExecutor-1] org.springboot.processor.MovieProcessor  : Converting MovieInfo [id=10012, title=Carnival Phantasm, description=How do you resolve a conflict between po ...] -> MovieInfo [id=10012, title=Carnival Phantasm, description=How do you resolve a conflict between po ...]
37.740  INFO 4024 --- [lTaskExecutor-1] org.springboot.processor.MovieProcessor  : Converting MovieInfo [id=9989, title=Ano Hi Mita Hana no Namae wo Bokutachi wa Mada Shiranai., description=After a tragic accident during their chi ...] -> MovieInfo [id=9989, title=Ano Hi Mita Hana no Namae wo Bokutachi wa Mada Shiranai., description=After a tragic accident during their chi ...]
37.740  INFO 4024 --- [lTaskExecutor-1] org.springboot.processor.MovieProcessor  : Converting MovieInfo [id=9996, title=Hyouge Mono, description=The story is set during Japan's Sengoku  ...] -> MovieInfo [id=9996, title=Hyouge Mono, description=The story is set during Japan's Sengoku  ...]
37.755  INFO 4024 --- [lTaskExecutor-1] o.s.batch.core.step.AbstractStep         : Step: [csvFileToDatabaseStep] executed in 93ms
37.755  INFO 4024 --- [lTaskExecutor-1] .s.b.l.JobCompletionNotificationListener : ============ JOB FINISHED ============
37.755  INFO 4024 --- [lTaskExecutor-1] .s.b.l.JobCompletionNotificationListener : Verifying the results...
37.755  INFO 4024 --- [lTaskExecutor-1] .s.b.l.JobCompletionNotificationListener : Found MovieInfo [id=1, title=Cowboy Bebop, description=In the year 2071, humanity has colonized ...] in the db
37.755  INFO 4024 --- [lTaskExecutor-1] .s.b.l.JobCompletionNotificationListener : Found MovieInfo [id=1000, title=Uchuu Kaizoku Captain Harlock, description=The year is 2977. Mankind has become com ...] in the db
37.755  INFO 4024 --- [lTaskExecutor-1] .s.b.l.JobCompletionNotificationListener : Found MovieInfo [id=10012, title=Carnival Phantasm, description=How do you resolve a conflict between po ...] in the db
37.755  INFO 4024 --- [lTaskExecutor-1] .s.b.l.JobCompletionNotificationListener : Found MovieInfo [id=9989, title=Ano Hi Mita Hana no Namae wo Bokutachi wa Mada Shiranai., description=After a tragic accident during their chi ...] in the db
37.755  INFO 4024 --- [lTaskExecutor-1] .s.b.l.JobCompletionNotificationListener : Found MovieInfo [id=9996, title=Hyouge Mono, description=The story is set during Japan's Sengoku  ...] in the db
37.755  INFO 4024 --- [lTaskExecutor-1] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=csvFileToDatabaseJob]] completed with the following parameters: [{name=job 0}] and the following status: [COMPLETED] in 109ms
40.627  INFO 4024 --- [           main] org.springboot.config.ShutdownBatchHook  : all jobs finished {0=COMPLETED}
40.627  INFO 4024 --- [           main] org.springboot.config.ShutdownBatchHook  : closing applicationContext
40.627  INFO 4024 --- [           main] o.s.s.c.ThreadPoolTaskScheduler          : Shutting down ExecutorService 'taskScheduler'
40.627  INFO 4024 --- [           main] o.springboot.SpringBatchCsvApplication   : gracefull shutdown
40.627  INFO 4024 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Shutting down ExecutorService 'threadPoolTaskExecutor'
40.627  INFO 4024 --- [           main] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
40.627  INFO 4024 --- [           main] .SchemaDropperImpl$DelayedDropActionImpl : HHH000477: Starting delayed evictData of schema as part of SessionFactory shut-down'
40.627  INFO 4024 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
40.643  INFO 4024 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.

```

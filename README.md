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

FlatFileItemReader is parameterized with a model.
setLineMapper method converts Strings to objects representing the item.
setResource. Public setter for the input resource.
setLinesToSkip the number of lines to skip at the start of a file.

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

Run as the standard Spring Boot app.

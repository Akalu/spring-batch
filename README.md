About
======

This is a simple project implementing asynchronous data processing on the basis of Spring Batch.

Notes
======

The most tricky moment is tied with correct shutdown of Spring Batch job schedulers - when other JDBC sources are used along with JPA this results in live threads after all jobs have completed their work.

This is done through introduction of ShutdownBatchHook singleton class which is used to track the state of all jobs and to invoke explicitly close() method on ConfigurableApplicationContext.  

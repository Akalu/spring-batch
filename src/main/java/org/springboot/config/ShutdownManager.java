package org.springboot.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShutdownManager {

	@Setter
	private ApplicationContext appContext;

	public void initiateShutdown(int returnCode) throws InterruptedException {
		log.info("initiateShutdown");
		Thread.sleep(5000);
		try {
			((ConfigurableApplicationContext) appContext).close();
		} catch (Exception e) {
			log.warn("there were errors during shutdown, the first exception {}", e.getMessage());
		}
	}
}
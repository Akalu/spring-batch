package org.springboot.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.batch.core.BatchStatus;
import org.springframework.context.ConfigurableApplicationContext;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.batch.core.BatchStatus.*;

@Slf4j
public class ShutdownBatchHook {

	private static final long pollTime = 3000;
	private static final Set<BatchStatus> completeSet = new HashSet<>(
			Arrays.asList(ABANDONED, COMPLETED, FAILED, STOPPED));

	@Setter
	private ConfigurableApplicationContext applicationContext;

	private ConcurrentMap<Long, BatchStatus> jobsMap = new ConcurrentHashMap<>();

	private ShutdownBatchHook() {
	}

	private static ShutdownBatchHook shutdownManager = null;

	synchronized public static ShutdownBatchHook get() {
		if (shutdownManager == null) {
			shutdownManager = new ShutdownBatchHook();
		}
		return shutdownManager;
	}

	public void upsertJob(Long id, BatchStatus status) {
		jobsMap.put(id, status);
	}

	public void waitForAllBatches(int returnCode) throws InterruptedException {
		log.info("initiate wait loop");
		while (hasLiveJobs()) {
			Thread.sleep(pollTime);
		}
		
		log.info("all jobs finished {}", jobsMap);
		log.info("closing applicationContext {}", applicationContext.getApplicationName());
		try {
			applicationContext.close();
		} catch (Exception e) {
			log.warn("there were errors during shutdown, the first exception {}", e.getMessage());
		}
	}

	public void closeNow(int returnCode) throws InterruptedException {
		log.info("initiate full stop with force");
		try {
			applicationContext.close();
		} catch (Exception e) {
			log.warn("there were errors during shutdown, the first exception {}", e.getMessage());
		}
	}

	private boolean hasLiveJobs() {
		for (Long id : jobsMap.keySet()) {
			if (!completeSet.contains(jobsMap.get(id))) {
				return true;
			}
		}
		return false;
	}
}
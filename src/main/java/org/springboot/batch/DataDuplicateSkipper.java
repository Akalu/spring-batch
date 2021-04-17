package org.springboot.batch;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.dao.DataIntegrityViolationException;

public class DataDuplicateSkipper implements SkipPolicy {

    @Override
    public boolean shouldSkip(Throwable exception, int skipCount) throws SkipLimitExceededException {
        if (exception instanceof DataIntegrityViolationException) {
            return true;
        }
        return true;
    }
}

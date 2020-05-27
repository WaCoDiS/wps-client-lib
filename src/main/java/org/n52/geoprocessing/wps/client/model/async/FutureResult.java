package org.n52.geoprocessing.wps.client.model.async;

import java.util.concurrent.CompletableFuture;

/**
 * Encapsulates a WPS result as {@link CompletableFuture} with corresponding WPS job ID
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class FutureResult {

    private String jobId;
    private CompletableFuture<Object> futureResult;

    public FutureResult(String jobId, CompletableFuture<Object> futureResult) {
        this.jobId = jobId;
        this.futureResult = futureResult;
    }

    public String getJobId() {
        return jobId;
    }

    public CompletableFuture<Object> getFutureResult() {
        return futureResult;
    }
}

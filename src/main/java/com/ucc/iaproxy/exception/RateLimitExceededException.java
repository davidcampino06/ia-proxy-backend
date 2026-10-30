package com.ucc.iaproxy.exception;

/**
 * Thrown when the user exceeds the requests per minute allowed by their plan (HTTP 429).
 */
public class RateLimitExceededException extends RuntimeException {

    private final long retryAfterSeconds;
    private final int limit;

    public RateLimitExceededException(int limit, long retryAfterSeconds) {
        super("You have exceeded the limit of " + limit + " requests per minute for your plan.");
        this.limit = limit;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }

    public int getLimit() {
        return limit;
    }
}

package com.ucc.iaproxy.exception;

/**
 * Thrown when the user runs out of their monthly token quota (HTTP 402).
 */
public class QuotaExceededException extends RuntimeException {

    private final long tokensUsed;
    private final long tokenLimit;

    public QuotaExceededException(long tokensUsed, long tokenLimit) {
        super("You have used up your monthly token quota. Upgrade your plan to continue.");
        this.tokensUsed = tokensUsed;
        this.tokenLimit = tokenLimit;
    }

    public long getTokensUsed() {
        return tokensUsed;
    }

    public long getTokenLimit() {
        return tokenLimit;
    }
}

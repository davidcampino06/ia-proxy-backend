package com.ucc.iaproxy.model;

/**
 * Available plans and their limits.
 * A value of -1 means unlimited (ENTERPRISE).
 */
public enum Plan {
    FREE(10, 50_000),
    PRO(60, 500_000),
    ENTERPRISE(-1, -1);

    private final int requestsPerMinute;
    private final long monthlyTokens;

    Plan(int requestsPerMinute, long monthlyTokens) {
        this.requestsPerMinute = requestsPerMinute;
        this.monthlyTokens = monthlyTokens;
    }

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public long getMonthlyTokens() {
        return monthlyTokens;
    }

    public boolean isUnlimitedRequests() {
        return requestsPerMinute < 0;
    }

    public boolean isUnlimitedTokens() {
        return monthlyTokens < 0;
    }
}

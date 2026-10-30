package com.ucc.iaproxy.model;

/**
 * Available plans and their limits.
 * A value of -1 means unlimited (ENTERPRISE).
 */
public enum Plan {
    FREE(10, 50_000, 0),
    PRO(60, 500_000, 19),
    ENTERPRISE(-1, -1, 99);

    private final int requestsPerMinute;
    private final long monthlyTokens;
    private final int monthlyPriceUsd;

    Plan(int requestsPerMinute, long monthlyTokens, int monthlyPriceUsd) {
        this.requestsPerMinute = requestsPerMinute;
        this.monthlyTokens = monthlyTokens;
        this.monthlyPriceUsd = monthlyPriceUsd;
    }

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public long getMonthlyTokens() {
        return monthlyTokens;
    }

    public int getMonthlyPriceUsd() {
        return monthlyPriceUsd;
    }

    public boolean isUnlimitedRequests() {
        return requestsPerMinute < 0;
    }

    public boolean isUnlimitedTokens() {
        return monthlyTokens < 0;
    }
}

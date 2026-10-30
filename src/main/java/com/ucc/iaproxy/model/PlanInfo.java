package com.ucc.iaproxy.model;

/**
 * Public description of a plan (response of GET /api/plans).
 * Null limits mean unlimited.
 */
public record PlanInfo(Plan id, Integer requestsPerMinute, Long monthlyTokens, int monthlyPriceUsd) {

    public static PlanInfo from(Plan plan) {
        return new PlanInfo(
                plan,
                plan.isUnlimitedRequests() ? null : plan.getRequestsPerMinute(),
                plan.isUnlimitedTokens() ? null : plan.getMonthlyTokens(),
                plan.getMonthlyPriceUsd());
    }
}

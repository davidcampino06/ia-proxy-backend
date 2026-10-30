package com.ucc.iaproxy.model;

import java.time.LocalDate;

/**
 * Quota status of a user (response of GET /api/quota/status).
 * Null limits mean unlimited.
 */
public record QuotaStatus(
        String userId,
        Plan plan,
        long tokensUsed,
        Long tokensRemaining,
        Long tokenLimit,
        LocalDate resetDate,
        int requestsThisMinute,
        Integer requestsPerMinuteLimit,
        long rateLimitResetInSeconds
) {
}

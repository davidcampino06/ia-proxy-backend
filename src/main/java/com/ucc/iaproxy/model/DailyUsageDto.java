package com.ucc.iaproxy.model;

import java.time.LocalDate;

/**
 * Usage for a single day (response of GET /api/quota/history).
 */
public record DailyUsageDto(LocalDate date, long tokens, int requests) {
}

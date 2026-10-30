package com.ucc.iaproxy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * Computes when the scheduled resets happen (see ResetScheduler):
 * - the rate limit resets at the start of every minute
 * - the monthly quota resets on the first day of every month
 */
public final class ResetClock {

    private ResetClock() {
    }

    /** Seconds left until the next rate limit reset (at least 1). */
    public static long secondsUntilNextMinute() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        long seconds = (long) Math.ceil(ChronoUnit.MILLIS.between(now, next) / 1000.0);
        return Math.max(1, seconds);
    }

    /** Date of the next monthly quota reset. */
    public static LocalDate nextMonthlyReset() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());
    }
}

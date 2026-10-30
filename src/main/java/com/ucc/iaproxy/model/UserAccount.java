package com.ucc.iaproxy.model;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Usage state of a single user (kept in memory).
 */
public class UserAccount {

    private final String userId;
    private Plan plan = Plan.FREE;
    private long tokensUsed;
    private int requestsThisMinute;
    private final Map<LocalDate, DailyUsage> history = new ConcurrentHashMap<>();

    public UserAccount(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public synchronized Plan getPlan() {
        return plan;
    }

    public synchronized void setPlan(Plan plan) {
        this.plan = plan;
    }

    public synchronized long getTokensUsed() {
        return tokensUsed;
    }

    public synchronized void addTokens(long tokens) {
        this.tokensUsed += tokens;
        history.computeIfAbsent(LocalDate.now(), DailyUsage::new).add(tokens);
    }

    public synchronized void resetTokens() {
        this.tokensUsed = 0;
    }

    public synchronized int getRequestsThisMinute() {
        return requestsThisMinute;
    }

    public synchronized void incrementRequests() {
        this.requestsThisMinute++;
    }

    public synchronized void resetRequests() {
        this.requestsThisMinute = 0;
    }

    public Map<LocalDate, DailyUsage> getHistory() {
        return history;
    }

    /**
     * Usage for a single day.
     */
    public static class DailyUsage {
        private final LocalDate date;
        private long tokens;
        private int requests;

        public DailyUsage(LocalDate date) {
            this.date = date;
        }

        public synchronized void add(long tokens) {
            this.tokens += tokens;
            this.requests++;
        }

        public LocalDate getDate() {
            return date;
        }

        public synchronized long getTokens() {
            return tokens;
        }

        public synchronized int getRequests() {
            return requests;
        }
    }
}

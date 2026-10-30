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

    /** Demo helper: sets the monthly token counter directly. */
    public synchronized void setTokensUsed(long tokensUsed) {
        this.tokensUsed = Math.max(0, tokensUsed);
    }

    /** Demo helper: adds tokens to the month and to today without counting a request. */
    public synchronized void addTokensWithoutRequest(long tokens) {
        this.tokensUsed += tokens;
        history.computeIfAbsent(LocalDate.now(), DailyUsage::new).addTokensOnly(tokens);
    }

    /** Demo helper: replaces the usage recorded for one day. */
    public synchronized void putDailyUsage(LocalDate date, long tokens, int requests) {
        DailyUsage usage = new DailyUsage(date);
        usage.set(tokens, requests);
        history.put(date, usage);
    }

    /** Demo helper: back to a brand new FREE account. */
    public synchronized void resetAll() {
        this.plan = Plan.FREE;
        this.tokensUsed = 0;
        this.requestsThisMinute = 0;
        this.history.clear();
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

        synchronized void addTokensOnly(long tokens) {
            this.tokens += tokens;
        }

        synchronized void set(long tokens, int requests) {
            this.tokens = tokens;
            this.requests = requests;
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

package com.ucc.iaproxy.service;

import com.ucc.iaproxy.model.Plan;
import com.ucc.iaproxy.model.UserAccount;
import com.ucc.iaproxy.store.UserStore;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Shortcuts for presenting the workshop: they put an account into a given state
 * without having to send thousands of prompts. They never bypass the proxies:
 * the next real request still goes through RateLimit -> Quota -> AI service.
 */
@Service
public class DemoService {

    private final UserStore userStore;

    public DemoService(UserStore userStore) {
        this.userStore = userStore;
    }

    /** Uses up the whole monthly quota, so the next prompt gets HTTP 402. */
    public void exhaustQuota(String userId) {
        UserAccount account = userStore.get(userId);
        Plan plan = account.getPlan();
        if (plan.isUnlimitedTokens()) {
            throw new IllegalArgumentException(plan + " has no monthly token limit to exhaust");
        }
        long missing = plan.getMonthlyTokens() - account.getTokensUsed();
        if (missing > 0) {
            // Recorded as today's usage so the history chart stays consistent
            account.addTokensWithoutRequest(missing);
        }
    }

    /**
     * Fills the previous 6 days with sample usage so the history chart has data.
     * The sample tokens are also counted in the monthly total (up to 60% of the plan).
     */
    public void seedHistory(String userId) {
        UserAccount account = userStore.get(userId);
        Plan plan = account.getPlan();
        long budget = plan.isUnlimitedTokens()
                ? 60_000
                : Math.max(0, (long) (plan.getMonthlyTokens() * 0.6) - account.getTokensUsed());

        LocalDate today = LocalDate.now();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long added = 0;
        for (int i = 6; i >= 1; i--) {
            LocalDate day = today.minusDays(i);
            long previous = account.getHistory().containsKey(day) ? account.getHistory().get(day).getTokens() : 0;
            long tokens = Math.min(budget / 6, random.nextLong(800, 6_000));
            int requests = tokens == 0 ? 0 : (int) Math.max(1, tokens / random.nextLong(45, 70));
            account.putDailyUsage(day, tokens, requests);
            if (day.getMonth() == today.getMonth()) {
                added += tokens - previous;
            }
        }
        account.setTokensUsed(account.getTokensUsed() + added);
    }

    /** Resets the account to a new FREE user with no usage. */
    public void reset(String userId) {
        userStore.get(userId).resetAll();
    }
}

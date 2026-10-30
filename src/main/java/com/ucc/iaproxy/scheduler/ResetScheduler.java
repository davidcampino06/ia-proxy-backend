package com.ucc.iaproxy.scheduler;

import com.ucc.iaproxy.model.UserAccount;
import com.ucc.iaproxy.store.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled tasks:
 * - Every minute (second 0) resets the request counter of every user.
 * - On the first day of every month at midnight resets the token quota.
 */
@Component
public class ResetScheduler {

    private static final Logger log = LoggerFactory.getLogger(ResetScheduler.class);

    private final UserStore userStore;

    public ResetScheduler(UserStore userStore) {
        this.userStore = userStore;
    }

    @Scheduled(cron = "0 * * * * *")
    public void resetRateLimits() {
        userStore.all().forEach(UserAccount::resetRequests);
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void resetMonthlyQuotas() {
        userStore.all().forEach(UserAccount::resetTokens);
        log.info("Monthly quotas reset for {} users", userStore.all().size());
    }
}

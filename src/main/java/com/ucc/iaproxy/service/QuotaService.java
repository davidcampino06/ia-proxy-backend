package com.ucc.iaproxy.service;

import com.ucc.iaproxy.model.DailyUsageDto;
import com.ucc.iaproxy.model.Plan;
import com.ucc.iaproxy.model.QuotaStatus;
import com.ucc.iaproxy.model.UserAccount;
import com.ucc.iaproxy.store.UserStore;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Status, history and plan upgrade operations.
 */
@Service
public class QuotaService {

    private final UserStore userStore;

    public QuotaService(UserStore userStore) {
        this.userStore = userStore;
    }

    public QuotaStatus status(String userId) {
        UserAccount account = userStore.get(userId);
        Plan plan = account.getPlan();
        long used = account.getTokensUsed();
        Long limit = plan.isUnlimitedTokens() ? null : plan.getMonthlyTokens();
        Long remaining = limit == null ? null : Math.max(0, limit - used);
        Integer rpm = plan.isUnlimitedRequests() ? null : plan.getRequestsPerMinute();

        return new QuotaStatus(
                userId, plan, used, remaining, limit,
                ResetClock.nextMonthlyReset(),
                account.getRequestsThisMinute(), rpm,
                ResetClock.secondsUntilNextMinute());
    }

    /** History of the last 7 days (including today), with zeros for days without usage. */
    public List<DailyUsageDto> history(String userId) {
        UserAccount account = userStore.get(userId);
        List<DailyUsageDto> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            UserAccount.DailyUsage usage = account.getHistory().get(day);
            result.add(usage == null
                    ? new DailyUsageDto(day, 0, 0)
                    : new DailyUsageDto(day, usage.getTokens(), usage.getRequests()));
        }
        return result;
    }

    /** Upgrades the user plan. Defaults to FREE -> PRO. */
    public QuotaStatus upgrade(String userId, Plan target) {
        UserAccount account = userStore.get(userId);
        Plan current = account.getPlan();
        Plan newPlan = target != null ? target : Plan.PRO;
        if (newPlan.ordinal() <= current.ordinal()) {
            throw new IllegalArgumentException("You can only upgrade to a plan higher than " + current);
        }
        account.setPlan(newPlan);
        return status(userId);
    }
}

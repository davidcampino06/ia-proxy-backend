package com.ucc.iaproxy.proxy;

import com.ucc.iaproxy.exception.RateLimitExceededException;
import com.ucc.iaproxy.model.GenerationRequest;
import com.ucc.iaproxy.model.GenerationResponse;
import com.ucc.iaproxy.model.Plan;
import com.ucc.iaproxy.model.UserAccount;
import com.ucc.iaproxy.service.AIGenerationService;
import com.ucc.iaproxy.service.ResetClock;
import com.ucc.iaproxy.store.UserStore;

/**
 * Proxy #1 in the chain: controls how many requests per minute a user can make.
 *
 * If the user exceeds their plan limit it throws RateLimitExceededException,
 * which GlobalExceptionHandler turns into HTTP 429 with the Retry-After header.
 * Otherwise it delegates the request to the next link (QuotaProxyService).
 */
public class RateLimitProxyService implements AIGenerationService {

    private final AIGenerationService next;
    private final UserStore userStore;

    public RateLimitProxyService(AIGenerationService next, UserStore userStore) {
        this.next = next;
        this.userStore = userStore;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserAccount account = userStore.get(request.getUserId());

        synchronized (account) {
            Plan plan = account.getPlan();
            if (!plan.isUnlimitedRequests()
                    && account.getRequestsThisMinute() >= plan.getRequestsPerMinute()) {
                throw new RateLimitExceededException(
                        plan.getRequestsPerMinute(), ResetClock.secondsUntilNextMinute());
            }
            account.incrementRequests();
        }

        // Request allowed: pass it to the next proxy in the chain
        return next.generate(request);
    }
}

package com.ucc.iaproxy.proxy;

import com.ucc.iaproxy.exception.QuotaExceededException;
import com.ucc.iaproxy.model.GenerationRequest;
import com.ucc.iaproxy.model.GenerationResponse;
import com.ucc.iaproxy.model.Plan;
import com.ucc.iaproxy.model.UserAccount;
import com.ucc.iaproxy.service.AIGenerationService;
import com.ucc.iaproxy.service.TokenEstimator;
import com.ucc.iaproxy.store.UserStore;

/**
 * Proxy #2 in the chain: controls the monthly token quota.
 *
 * Before calling the real service it checks that the user has enough tokens
 * for the prompt; if not, it throws QuotaExceededException (HTTP 402 Payment Required).
 * After the response it deducts the consumed tokens (prompt + completion)
 * and records the usage in the daily history.
 */
public class QuotaProxyService implements AIGenerationService {

    private final AIGenerationService realService;
    private final UserStore userStore;

    public QuotaProxyService(AIGenerationService realService, UserStore userStore) {
        this.realService = realService;
        this.userStore = userStore;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserAccount account = userStore.get(request.getUserId());
        Plan plan = account.getPlan();
        long promptTokens = TokenEstimator.estimate(request.getPrompt());

        // 1. Pre-check: the user must have quota left for this prompt
        if (!plan.isUnlimitedTokens()
                && account.getTokensUsed() + promptTokens > plan.getMonthlyTokens()) {
            throw new QuotaExceededException(account.getTokensUsed(), plan.getMonthlyTokens());
        }

        // 2. Delegate to the real service
        GenerationResponse response = realService.generate(request);

        // 3. Deduct the consumed tokens and record the usage
        long completionTokens = TokenEstimator.estimate(response.getText());
        long total = promptTokens + completionTokens;
        account.addTokens(total);

        response.setPromptTokens(promptTokens);
        response.setCompletionTokens(completionTokens);
        response.setTotalTokens(total);
        response.setRemainingTokens(plan.isUnlimitedTokens()
                ? null
                : Math.max(0, plan.getMonthlyTokens() - account.getTokensUsed()));
        return response;
    }
}

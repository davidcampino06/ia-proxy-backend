package com.ucc.iaproxy;

import com.ucc.iaproxy.exception.QuotaExceededException;
import com.ucc.iaproxy.exception.RateLimitExceededException;
import com.ucc.iaproxy.model.GenerationRequest;
import com.ucc.iaproxy.model.GenerationResponse;
import com.ucc.iaproxy.model.Plan;
import com.ucc.iaproxy.proxy.QuotaProxyService;
import com.ucc.iaproxy.proxy.RateLimitProxyService;
import com.ucc.iaproxy.service.AIGenerationService;
import com.ucc.iaproxy.store.UserStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the RateLimit -> Quota -> real service chain.
 * A fake instant service is used so the tests do not wait for the mock's 1200 ms.
 */
class ProxyChainTest {

    private UserStore store;
    private AIGenerationService chain;
    private int realCalls;

    @BeforeEach
    void setUp() {
        store = new UserStore();
        realCalls = 0;
        AIGenerationService fakeReal = request -> {
            realCalls++;
            return new GenerationResponse("abcd".repeat(10)); // 10 tokens
        };
        chain = new RateLimitProxyService(new QuotaProxyService(fakeReal, store), store);
    }

    @Test
    void validRequestDeductsTokens() {
        GenerationResponse r = chain.generate(new GenerationRequest("u1", "abcdefgh")); // 2 tokens
        assertEquals(12, r.getTotalTokens());
        assertEquals(12, store.get("u1").getTokensUsed());
        assertEquals(50_000 - 12, r.getRemainingTokens());
    }

    @Test
    void freePlanBlocksEleventhRequestInSameMinute() {
        for (int i = 0; i < 10; i++) {
            chain.generate(new GenerationRequest("u2", "hello"));
        }
        RateLimitExceededException ex = assertThrows(RateLimitExceededException.class,
                () -> chain.generate(new GenerationRequest("u2", "hello")));
        assertTrue(ex.getRetryAfterSeconds() >= 1 && ex.getRetryAfterSeconds() <= 60);
        assertEquals(10, realCalls, "The real service must not receive the blocked request");
    }

    @Test
    void exhaustedQuotaThrowsAndSkipsRealService() {
        store.get("u3").addTokens(50_000);
        assertThrows(QuotaExceededException.class,
                () -> chain.generate(new GenerationRequest("u3", "hello")));
        assertEquals(0, realCalls);
    }

    @Test
    void enterpriseHasNoLimits() {
        store.get("u4").setPlan(Plan.ENTERPRISE);
        store.get("u4").addTokens(10_000_000);
        for (int i = 0; i < 100; i++) {
            chain.generate(new GenerationRequest("u4", "hello"));
        }
        assertEquals(100, realCalls);
    }

    @Test
    void minuteResetAllowsSendingAgain() {
        for (int i = 0; i < 10; i++) {
            chain.generate(new GenerationRequest("u5", "hello"));
        }
        store.get("u5").resetRequests();
        assertDoesNotThrow(() -> chain.generate(new GenerationRequest("u5", "hello")));
    }
}

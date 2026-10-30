package com.ucc.iaproxy.config;

import com.ucc.iaproxy.proxy.QuotaProxyService;
import com.ucc.iaproxy.proxy.RateLimitProxyService;
import com.ucc.iaproxy.service.AIGenerationService;
import com.ucc.iaproxy.service.MockAIGenerationService;
import com.ucc.iaproxy.store.UserStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds the proxy chain:
 *
 *   Cliente -> RateLimitProxyService -> QuotaProxyService -> MockAIGenerationService
 *
 * The controller only receives an AIGenerationService and does not know there are proxies behind it.
 */
@Configuration
public class ProxyChainConfig {

    @Bean
    public AIGenerationService aiGenerationService(UserStore userStore) {
        AIGenerationService realService = new MockAIGenerationService();
        AIGenerationService quotaProxy = new QuotaProxyService(realService, userStore);
        return new RateLimitProxyService(quotaProxy, userStore);
    }
}

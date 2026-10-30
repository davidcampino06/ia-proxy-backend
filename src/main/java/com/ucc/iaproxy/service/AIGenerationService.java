package com.ucc.iaproxy.service;

import com.ucc.iaproxy.model.GenerationRequest;
import com.ucc.iaproxy.model.GenerationResponse;

/**
 * Common interface (Subject) of the Proxy pattern.
 * It is implemented both by the real service (MockAIGenerationService)
 * and by the proxies (RateLimitProxyService and QuotaProxyService),
 * so the client never knows whether it is talking to the real object or a proxy.
 */
public interface AIGenerationService {

    GenerationResponse generate(GenerationRequest request);
}

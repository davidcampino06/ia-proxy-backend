package com.ucc.iaproxy.service;

import com.ucc.iaproxy.model.GenerationRequest;
import com.ucc.iaproxy.model.GenerationResponse;
import com.ucc.iaproxy.model.ProxyStep;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Real object (RealSubject) of the Proxy pattern.
 * Simulates an AI service: waits 1200 ms and returns a random predefined text.
 */
public class MockAIGenerationService implements AIGenerationService {

    public static final String LAYER = "MockAIGenerationService";

    private static final long SIMULATED_LATENCY_MS = 1200;

    private static final List<String> RESPONSES = List.of(
            "The Proxy pattern acts as an intermediary that controls access to a real object, "
                    + "making it possible to add validation, caching or lazy loading without changing the client.",
            "Generative AI produces text from patterns learned on large amounts of data. "
                    + "Every response consumes tokens, which is why controlling quotas matters.",
            "A rate limiter protects a service from being overloaded: it caps how many requests "
                    + "a user can make in a time window, usually answering with HTTP 429.",
            "Spring Boot makes it easy to build REST APIs with minimal configuration. With @Scheduled "
                    + "you can run periodic tasks such as resetting usage counters.",
            "Chaining proxies separates responsibilities: one limits request frequency, "
                    + "another enforces the monthly quota, and finally the real service does the work.",
            "Subscription plans (FREE, PRO, ENTERPRISE) are a common model in AI platforms: "
                    + "each plan offers more requests per minute and more tokens per month.",
            "Hi! I am a simulated AI service. I do not really understand your question, but I reply "
                    + "with a random text after thinking for 1.2 seconds to mimic real latency.",
            "A good software design practice is to depend on interfaces rather than concrete "
                    + "implementations, so the real service can be swapped for a proxy without the client noticing."
    );

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        long start = System.nanoTime();
        try {
            Thread.sleep(SIMULATED_LATENCY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String text = RESPONSES.get(ThreadLocalRandom.current().nextInt(RESPONSES.size()));
        GenerationResponse response = new GenerationResponse(text);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        response.addStep(new ProxyStep(LAYER, "Generated a reply (simulated model)", elapsedMs));
        return response;
    }
}

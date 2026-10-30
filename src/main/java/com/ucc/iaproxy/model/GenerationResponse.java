package com.ucc.iaproxy.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Text generation response.
 * The real service fills in the text; QuotaProxyService adds the consumed and remaining tokens.
 * Every layer of the chain records a {@link ProxyStep} so the client can see the full path.
 */
public class GenerationResponse {

    private String text;
    private long promptTokens;
    private long completionTokens;
    private long totalTokens;
    private Long remainingTokens; // null = unlimited
    private Instant timestamp;
    private final List<ProxyStep> steps = new ArrayList<>();

    public GenerationResponse() {
    }

    public GenerationResponse(String text) {
        this.text = text;
        this.timestamp = Instant.now();
    }

    /**
     * Adds a step at the beginning of the list. Proxies call this after the inner
     * layer returns, so the final order is the order the request travelled.
     */
    public void prependStep(ProxyStep step) {
        steps.add(0, step);
    }

    public void addStep(ProxyStep step) {
        steps.add(step);
    }

    public List<ProxyStep> getSteps() {
        return steps;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(long promptTokens) {
        this.promptTokens = promptTokens;
    }

    public long getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(long completionTokens) {
        this.completionTokens = completionTokens;
    }

    public long getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(long totalTokens) {
        this.totalTokens = totalTokens;
    }

    public Long getRemainingTokens() {
        return remainingTokens;
    }

    public void setRemainingTokens(Long remainingTokens) {
        this.remainingTokens = remainingTokens;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}

package com.ucc.iaproxy.model;

import java.time.Instant;

/**
 * Text generation response.
 * The real service fills in the text; QuotaProxyService adds the consumed and remaining tokens.
 */
public class GenerationResponse {

    private String text;
    private long promptTokens;
    private long completionTokens;
    private long totalTokens;
    private Long remainingTokens; // null = unlimited
    private Instant timestamp;

    public GenerationResponse() {
    }

    public GenerationResponse(String text) {
        this.text = text;
        this.timestamp = Instant.now();
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

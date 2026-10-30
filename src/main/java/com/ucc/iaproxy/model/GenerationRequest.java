package com.ucc.iaproxy.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Text generation request.
 * The userId is set by the controller from the X-User-Id header.
 */
public class GenerationRequest {

    private String userId;

    @NotBlank(message = "Prompt must not be empty")
    @Size(max = 4000, message = "Prompt must not exceed 4000 characters")
    private String prompt;

    public GenerationRequest() {
    }

    public GenerationRequest(String userId, String prompt) {
        this.userId = userId;
        this.prompt = prompt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}

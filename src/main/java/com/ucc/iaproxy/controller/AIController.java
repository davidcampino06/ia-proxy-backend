package com.ucc.iaproxy.controller;

import com.ucc.iaproxy.model.GenerationRequest;
import com.ucc.iaproxy.model.GenerationResponse;
import com.ucc.iaproxy.service.AIGenerationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The controller is the "client" of the Proxy pattern: it only knows the
 * AIGenerationService interface. Spring injects the full proxy chain.
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIGenerationService aiGenerationService;

    public AIController(AIGenerationService aiGenerationService) {
        this.aiGenerationService = aiGenerationService;
    }

    @PostMapping("/generate")
    public GenerationResponse generate(
            @RequestHeader(value = "X-User-Id", defaultValue = "demo") String userId,
            @Valid @RequestBody GenerationRequest request) {
        request.setUserId(userId);
        return aiGenerationService.generate(request);
    }
}

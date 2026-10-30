package com.ucc.iaproxy.controller;

import com.ucc.iaproxy.model.DailyUsageDto;
import com.ucc.iaproxy.model.Plan;
import com.ucc.iaproxy.model.QuotaStatus;
import com.ucc.iaproxy.service.QuotaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quota")
public class QuotaController {

    private final QuotaService quotaService;

    public QuotaController(QuotaService quotaService) {
        this.quotaService = quotaService;
    }

    /** Used tokens, remaining tokens, reset date and current plan. */
    @GetMapping("/status")
    public QuotaStatus status(@RequestHeader(value = "X-User-Id", defaultValue = "demo") String userId) {
        return quotaService.status(userId);
    }

    /** Daily usage for the last 7 days. */
    @GetMapping("/history")
    public List<DailyUsageDto> history(@RequestHeader(value = "X-User-Id", defaultValue = "demo") String userId) {
        return quotaService.history(userId);
    }

    /** Plan upgrade (FREE -> PRO by default). Optional body: {"plan": "ENTERPRISE"} */
    @PostMapping("/upgrade")
    public QuotaStatus upgrade(
            @RequestHeader(value = "X-User-Id", defaultValue = "demo") String userId,
            @RequestBody(required = false) UpgradeRequest body) {
        return quotaService.upgrade(userId, body == null ? null : body.plan());
    }

    public record UpgradeRequest(Plan plan) {
    }
}

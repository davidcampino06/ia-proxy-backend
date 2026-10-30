package com.ucc.iaproxy.controller;

import com.ucc.iaproxy.model.QuotaStatus;
import com.ucc.iaproxy.service.DemoService;
import com.ucc.iaproxy.service.QuotaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Demo shortcuts for presenting the workshop.
 * Can be turned off with the DEMO_ENABLED=false environment variable.
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final DemoService demoService;
    private final QuotaService quotaService;
    private final boolean enabled;

    public DemoController(DemoService demoService, QuotaService quotaService,
                          @Value("${app.demo.enabled}") boolean enabled) {
        this.demoService = demoService;
        this.quotaService = quotaService;
        this.enabled = enabled;
    }

    /** Uses up the monthly quota so the next prompt is rejected with 402. */
    @PostMapping("/exhaust-quota")
    public QuotaStatus exhaustQuota(@RequestHeader(value = "X-User-Id", defaultValue = "demo") String userId) {
        checkEnabled();
        demoService.exhaustQuota(userId);
        return quotaService.status(userId);
    }

    /** Fills the last 6 days with sample usage for the history chart. */
    @PostMapping("/seed-history")
    public QuotaStatus seedHistory(@RequestHeader(value = "X-User-Id", defaultValue = "demo") String userId) {
        checkEnabled();
        demoService.seedHistory(userId);
        return quotaService.status(userId);
    }

    /** Back to a brand new FREE account. */
    @PostMapping("/reset")
    public QuotaStatus reset(@RequestHeader(value = "X-User-Id", defaultValue = "demo") String userId) {
        checkEnabled();
        demoService.reset(userId);
        return quotaService.status(userId);
    }

    private void checkEnabled() {
        if (!enabled) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Demo tools are disabled");
        }
    }
}

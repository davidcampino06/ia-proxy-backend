package com.ucc.iaproxy.controller;

import com.ucc.iaproxy.model.Plan;
import com.ucc.iaproxy.model.PlanInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class PlanController {

    /** All plans with their limits and price. */
    @GetMapping("/api/plans")
    public List<PlanInfo> plans() {
        return Arrays.stream(Plan.values()).map(PlanInfo::from).toList();
    }
}

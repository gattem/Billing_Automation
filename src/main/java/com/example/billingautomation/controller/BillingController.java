package com.example.billingautomation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.billingautomation.dto.ScenarioRequest;
import com.example.billingautomation.service.BillingService;

@RestController
@RequestMapping("/api/v1/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Billing Automation service is up");
    }

    @PostMapping("/precheck")
    public ResponseEntity<Object> precheck(@RequestBody ScenarioRequest request) {
        return ResponseEntity.ok(billingService.precheck(request));
    }

    @PostMapping("/scenario/run")
    public ResponseEntity<Object> runScenario(@RequestBody ScenarioRequest request) {
        return ResponseEntity.ok(billingService.runScenario(request));
    }
}

package com.example.billingautomation.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.billingautomation.dto.ScenarioRequest;
import com.example.billingautomation.repository.DocumentRepository;

@Service
public class BillingService {

    private final DocumentRepository documentRepository;

    public BillingService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Map<String, Object> precheck(ScenarioRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("accountId", request.accountId);
        result.put("monthYear", request.monthYear);
        result.put("scenarioType", request.scenarioType);
        result.put("finalBillExists", documentRepository.existsById(request.finalBillId()));
        result.put("testBillExists", documentRepository.existsById(request.testBillId()));
        result.put("adjustmentDocExists", documentRepository.existsById(request.adjustmentDocId()));
        return result;
    }

    public Map<String, Object> runScenario(ScenarioRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("scenarioType", request.scenarioType);
        result.put("status", "started");
        result.put("message", "Scenario execution is not yet implemented. Backend skeleton created.");
        return result;
    }
}

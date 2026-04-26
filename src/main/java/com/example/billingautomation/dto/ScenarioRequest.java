package com.example.billingautomation.dto;

public class ScenarioRequest {
    public String accountId;
    public String monthYear;
    public String billDate;
    public String billDueDate;
    public String fromDate;
    public String toDate;
    public String billGenerationIp;
    public String billInvoiceIp;
    public String scenarioType;

    public String adjustmentDocId() {
        return String.format("3006_bsUAD::%s_%s", accountId, monthYear);
    }

    public String finalBillId() {
        return String.format("2001_bsBGI::%s_%s", accountId, monthYear);
    }

    public String testBillId() {
        return String.format("2001_bsBGI::%s_%s_TESTBILLRUN", accountId, monthYear);
    }
}

package com.example.billingautomation.model;

import java.util.Map;

public class BillingAccountDoc {
    public String id;
    public String accountId;
    public SubsDetails subsDetails;
    public CancelledInstallmentService cancelledInstallmentService;
    public boolean billGenerationRequired;
    public boolean testBillGenRequired;
    public String bucketId;

    public static class SubsDetails {
        public Map<String, SubscriberDetail> activeSubsDetails;
        public Map<String, SubscriberDetail> cancelledSubsDetails;
    }

    public static class SubscriberDetail {
        public String lineTerminationDate;
        public String lastUpdateDate;
        public String lineActivationDate;
        public String status;
        public String transferOutDate;
        public String transferInDate;
    }

    public static class CancelledInstallmentService {
        public Map<String, InstallmentLine> activeLineId;
        public Map<String, InstallmentLine> cancelledLineId;
    }

    public static class InstallmentLine {
        public String lineIdStartDate;
        public String lineIdEndDate;
        public String lineIdStatus;
    }
}

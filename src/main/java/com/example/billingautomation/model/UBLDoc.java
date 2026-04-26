package com.example.billingautomation.model;

import java.util.List;

public class UBLDoc {
    public String id;
    public Integer month;
    public Integer year;
    public String accountId;
    public String billCalculated;
    public List<SubscriberPlan> subscriberPlan;

    public static class SubscriberPlan {
        public String subscriberNumber;
        public List<Package> packages;
    }

    public static class Package {
        public String packageId;
        public String packageType;
    }
}

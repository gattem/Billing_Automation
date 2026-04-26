package com.example.billingautomation.model;

import java.util.List;

public class BillPlanDoc {
    public String id;
    public String accountId;
    public Integer invoiceCounter;
    public RecurringCharges recurringCharges;

    public static class RecurringCharges {
        public List<Package> packages;
    }

    public static class Package {
        public String packageId;
        public String packageInstanceId;
        public String packageType;
        public String packageEffectiveStartDate;
        public String packageProrateFlag;
        public boolean oneTimePaymentStatus;
        public int oneTimeCharges;
        public int packageCurrentSlabCounter;
        public PackageDiscount packageDiscount;
    }

    public static class PackageDiscount {
        public int currentDiscountCounter;
    }
}

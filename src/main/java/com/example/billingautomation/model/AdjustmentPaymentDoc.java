package com.example.billingautomation.model;

import java.util.List;

public class AdjustmentPaymentDoc {
    public String id;
    public Integer month;
    public Integer year;
    public String accountId;
    public List<Adjustment> adjustments;
    public List<Payment> payments;
    public PreviousMonthBill previousMonthBill;

    public static class Adjustment {
        public boolean adjustmentAmountIncludesTax;
        public String adjustmentTaxType;
        public int adjustmentTaxValue;
        public String creationDate;
        public Credit credit;
        public Object debit;
        public Object discount;
        public String subscriberNumber;
        public String transactionRef;

        public static class Credit {
            public int creditAmount;
            public String creditDescription;
            public String creditType;
            public String glCode;
        }
    }

    public static class Payment {
        public String paymentDate;
        public int paymentAmount;
        public String paymentMode;
        public String paymentRef;
        public boolean paymentBilled;
    }

    public static class PreviousMonthBill {
        public int billAmount;
        public String billDueDate;
        public int billTax;
    }
}

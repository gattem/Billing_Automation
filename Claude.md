# Billing Automation UI - README

## Purpose

This document converts the manual billing operations into a basic UI-driven workflow.

The UI should help an operations user:

- validate prerequisites
- view relevant document IDs
- run test bill generation
- validate bill output with BU
- trigger final bill generation
- trigger invoice creation
- handle special cases like cancelled lines and installments

## Recommended UI Structure

Build a simple web UI with these sections:

1. **Input Form**
2. **Scenario Selector**
3. **Pre-checks Panel**
4. **Document Actions Panel**
5. **API Trigger Panel**
6. **Validation Checklist**
7. **Execution Log / Audit Trail**

## Required Inputs

The UI should collect:

- `accountId`
- `monthYear` in `MMYY` format
- `billDate`
- `billDueDate`
- `fromDate`
- `toDate`
- `billGenerationIp`
- `billInvoiceIp`
- `scenarioType`

Suggested scenario values:

- `before_final_bill_trigger`
- `after_final_bill_trigger_with_cancel_lines`
- `active_and_cancel_installments`

## Common Document References

These references are based on the sample Couchbase docs in `sample_couchbase_docs`.

### Actual JSON `id` patterns seen in the samples

- `3006_bsUAD::<accountId>_<MMYY>`
- `2001_bsBGI::<accountId>_<MMYY>`
- `2001_bsBGI::<accountId>_<MMYY>_TESTBILLRUN`
- `3001_bsBillAcc::<accountId>`
- `3002_bsBillPlan::<msisdn>`
- `2002_bsUBL::<msisdn>_<MMYY>`
- installment sample document `id` is `LI01336080`

### Sample file naming patterns in this folder

- `3002_bsBillPlan--<msisdn>_<MMYY>_D.txt`
- `2002_bsUBL--<msisdn>_<MMYY>_D.txt`
- `3009_bsInstallment--<accountId>_<msisdn>_ins.txt`

### Important note

- for `3002` and `3009`, the sample file names contain more context than the actual JSON `id`
- at least in the samples, `_D`, `MMYY`, and `accountId` are not always part of the JSON `id`
- the UI should display both:
  - document `id` from Couchbase
  - operator-friendly label based on file name or business context

## Sample Data Summary

The sample files currently represent these business values:

- sample account ID: `111100000339`
- current bill month examples: `0325`, `0425`, `0326`, `0426`
- active line sample: `07093558953`
- cancelled line sample: `07083313198`
- installment line sample: `07032715200`

Sample documents available:

- final bill: `2001_bsBGI::111100000339_0325`
- test bill: `2001_bsBGI::111100000339_0325_TESTBILLRUN`
- current month UAD: `3006_bsUAD::111100000339_0325`
- future month UAD: `3006_bsUAD::111100000339_0425`
- billing account: `3001_bsBillAcc::111100000339`
- active bill plan sample: `3002_bsBillPlan::07093558953`
- cancelled line bill plan sample file: `3002_bsBillPlan--07083313198_0326_D.txt`
- active UBL samples:
  - `2002_bsUBL::07093558953_0326`
  - `2002_bsUBL::07093558953_0426`
- cancelled line UBL sample files:
  - `2002_bsUBL--07083313198_0326_D.txt`
  - `2002_bsUBL--07083313198_0426_D.txt`
- installment sample file:
  - `3009_bsInstallment--111100000339_07032715200_ins.txt`

## Sample-Based Field Mapping

The sample Couchbase docs confirm these actual JSON paths.

### 1. Billing Account Document

Document:

- `3001_bsBillAcc::<accountId>`

Important paths:

- `subsDetails.activeSubsDetails`
- `subsDetails.cancelledSubsDetails`
- `cancelledInstallmentService.activeLineId`
- `cancelledInstallmentService.cancelledLineId`
- `billGenerationRequired`
- `testBillGenRequired`
- `bucketId`

Notes:

- active and cancelled subscriber lines are stored under `subsDetails`
- installment line mapping is stored separately under `cancelledInstallmentService`
- when moving cancelled lines back to active lines, the UI should update:
  - `subsDetails.cancelledSubsDetails`
  - `subsDetails.activeSubsDetails`

### 2. Adjustment And Payment Document

Document:

- `3006_bsUAD::<accountId>_<MMYY>`

Important paths:

- `adjustments`
- `payments`
- `payments[].paymentDate`
- `payments[].paymentAmount`
- `payments[].paymentMode`
- `payments[].paymentRef`
- `payments[].paymentBilled`
- `previousMonthBill`

Notes:

- the payment billed toggle is not under a nested `v` object
- use `payments[].paymentBilled`
- current month sample shows `paymentBilled=true`
- future month sample shows `paymentBilled=false`

### 3. Bill Plan Document

Document:

- `3002_bsBillPlan::<msisdn>`

Sample examples:

- JSON `id`: `3002_bsBillPlan::07093558953`
- sample cancelled-line file: `3002_bsBillPlan--07083313198_0326_D.txt`

Important paths:

- `invoiceCounter`
- `recurringCharges.packages`
- `recurringCharges.packages[].packageId`
- `recurringCharges.packages[].packageType`
- `recurringCharges.packages[].packageEffectiveStartDate`
- `recurringCharges.packages[].packageProrateFlag`
- `recurringCharges.packages[].oneTimePaymentStatus`
- `recurringCharges.packages[].oneTimeCharges`
- `recurringCharges.packages[].packageCurrentSlabCounter`
- `recurringCharges.packages[].packageDiscount.currentDiscountCounter`

Notes:

- the correct field is `oneTimePaymentStatus`, not `v.oneTimePaymentStatus`
- the correct field is `packageProrateFlag`, not `v.packageProrateFlag`
- the correct counter path is `packageDiscount.currentDiscountCounter`
- the correct package counter path is `packageCurrentSlabCounter`
- counter updates must happen inside each object in `recurringCharges.packages[]`
- sample file names may contain `_MMYY_D`, but the JSON `id` does not include that suffix in the sample payload

### 4. Installment Document

Document:

- sample file name: `3009_bsInstallment--<accountId>_<msisdn>_ins.txt`
- sample JSON `id`: `LI01336080`

Important paths:

- `invoiceCounter`
- `recurringCharges.packages`
- `recurringCharges.packages[].packageType`
- `recurringCharges.packages[].packageProrateFlag`
- `recurringCharges.packages[].oneTimePaymentStatus`
- `recurringCharges.packages[].packageCurrentSlabCounter`
- `recurringCharges.packages[].packageDiscount.currentDiscountCounter`

Notes:

- installment-related packages in the sample are identified by `packageType="installment"`
- scenario 3 counter decrement should be applied inside `recurringCharges.packages[]`
- the sample does not confirm a Couchbase `id` pattern like `3009_bsInstallment::<accountId>_<msisdn>_ins`

### 5. UBL Document

Document:

- `2002_bsUBL::<msisdn>_<MMYY>`

Sample examples:

- active line sample JSON `id`: `2002_bsUBL::07093558953_0326`
- cancelled line sample file: `2002_bsUBL--07083313198_0326_D.txt`

Important paths:

- `billCalculated`
- `subscriberPlan`
- `subscriberPlan[].packages`

Notes:

- the bill calculation reset uses `billCalculated`
- sample shows values like `yes` and `no`
- sample cancelled-line file names contain `_D`, but the JSON `id` in the sample payload does not

### 6. Final Bill / Test Bill Document

Document:

- `2001_bsBGI::<accountId>_<MMYY>`
- `2001_bsBGI::<accountId>_<MMYY>_TESTBILLRUN`

Important paths:

- `invoiceDetails.invoiceNumber`
- `invoiceDetails.billDate`
- `invoiceDetails.billDueDate`
- `invoiceDetails.billPeriod.fromDate`
- `invoiceDetails.billPeriod.toDate`
- `paymentMode`
- `subscribersInfo`

Notes:

- BU validation can compare final bill and test bill using these bill output fields
- payment history appears inside the generated bill output, not in `3006`

## Common API Calls

### 1. Generate Test Bill

```bash
curl -gkv "http://[<billGenerationIp>]:8088/billing/bgs/processBillEvent" \
  --header "Content-Type: application/json" \
  --request POST \
  --data '{
    "accountId": "<accountId>",
    "billRunMode": "testbillrun",
    "monthYear": "<MMYY>",
    "billDate": "<billDate>",
    "billDueDate": "<billDueDate>",
    "fromDate": "<fromDate>",
    "toDate": "<toDate>"
  }'
```

### 2. Generate Final Bill Without Email

```bash
curl -gkv "http://[<billGenerationIp>]:8088/billing/bgs/processBillEvent" \
  --header "Content-Type: application/json" \
  --request POST \
  --data '{
    "accountId": "<accountId>",
    "billRunMode": "finalbillrun",
    "monthYear": "<MMYY>",
    "billDate": "<billDate>",
    "billDueDate": "<billDueDate>",
    "fromDate": "<fromDate>",
    "toDate": "<toDate>",
    "emailRequired": false
  }'
```

### 3. Create Invoice Preview / Validate Final Bill

```bash
curl -v --location --request POST "http://[<billInvoiceIp>]:8090/billinvoice/api/v1/billing/createInvoice/test" \
  --header "Content-Type: application/json" \
  --data-raw '{
    "accountId": "<accountId>",
    "documentId": "2001_bsBGI::<accountId>_<MMYY>",
    "invoiceType": "FINAL_BILL"
  }'
```

### 4. Process Cancelled Lines

```bash
curl --location "http://[<billGenerationIp>]:8088/billing/bgs/processCancelledLines" \
  --header "Content-Type: application/json" \
  --data '{
    "documentId": "2008_bsCancelledLines::<accountId>_<MMYY>"
  }'
```

## Scenario 1: Bill Generation Before Final Bill Trigger

### Flow

1. Check whether adjustment is updated in `3006_bsUAD::<accountId>_<MMYY>`.
2. Check whether final bill already exists.
3. If final bill is not generated:
   - take backup of `2001_bsBGI::<accountId>_<MMYY>_TESTBILLRUN`
   - trigger test bill generation
4. Validate whether adjustments are reflected in `2001_bsBGI::<accountId>_<MMYY>_TESTBILLRUN`.
5. Get confirmation from BU that test bill is correct.
6. Trigger final bill generation with `emailRequired=false`.
7. Ask BU to validate final bill through invoice preview API.

### UI Actions

- `Check Adjustment`
- `Check Final Bill Status`
- `Backup Test Bill Doc`
- `Generate Test Bill`
- `Mark BU Validation Complete`
- `Generate Final Bill`
- `Create Invoice Preview`

## Scenario 2: Bill Generation After Final Bill Trigger With Cancel Lines

### Flow

1. Check whether adjustment is updated in `3006_bsUAD::<accountId>_<MMYY>`.
2. Confirm that final bill is already generated.
3. In `3001_bsBillAcc::<accountId>`, move cancelled lines from `cancelledSubsDetails` to `activeSubsDetails` where `lineTerminationDate` belongs to the billing month.
4. Re-create cancel-line documents without `_D` suffix:
   - sample operator file: `2002_bsUBL--<msisdn>_<MMYY>_D.txt` should be restored to the normal `2002_bsUBL::<msisdn>_<MMYY>` billing state
   - sample operator file: `2002_bsUBL--<msisdn>_<nextMMYY>_D.txt` should be restored to the normal `2002_bsUBL::<msisdn>_<nextMMYY>` billing state
   - sample operator file: `3002_bsBillPlan--<msisdn>_<MMYY>_D.txt` maps to JSON `id` pattern `3002_bsBillPlan::<msisdn>`
5. In all matching `3002` docs:
   - set `recurringCharges.packages[].oneTimePaymentStatus=false`
   - only for `packageId="bcc32bdb478c34b29814c8699055b6ff"`
   - only when `recurringCharges.packages[].oneTimeCharges != 0`
   - only when `recurringCharges.packages[].packageEffectiveStartDate` falls in the same bill month
6. For package IDs below, change `recurringCharges.packages[].packageProrateFlag` from `prorate` to `bill_current_cycle_fully` when start date falls in the same bill month:
   - `dd1e9c9f2490386086cc91f296112478` for universal fee
   - `109f3bd57a2237caad1e16930b9ce696` for relay service fee
   - also set `recurringCharges.packages[].oneTimePaymentStatus=false`
7. Decrease counters without crossing minimum values:
   - `invoiceCounter` minimum `1` at document level
   - `recurringCharges.packages[].packageCurrentSlabCounter` minimum `0`
   - `recurringCharges.packages[].packageDiscount.currentDiscountCounter` minimum `1`
8. Update all related `2002_bsUBL::<msisdn>_<MMYY>` docs and change `billCalculated` from `yes` to `no`.
9. In current month `3006_bsUAD::<accountId>_<MMYY>`, change `payments[].paymentBilled` to `false` and make sure the same payment is not billed from previous month `2001` doc.
10. In next month `3006_bsUAD::<accountId>_<nextMMYY>`, change `payments[].paymentBilled` to `true` and make sure the same payment is not billed in current month `2001` doc.
11. Trigger test bill generation.
12. Validate test bill with BU. If correct:
   - delete existing final bill doc
   - trigger final bill generation without email
13. Ask BU to validate final bill through invoice preview API.
14. Revert payment billed flag in next month `3006` doc back to `false`.
15. Update `1001` doc with future month payment.
16. If bill is generated after 26th of the month, remove `schedulerReferenceId` from billing account doc.
17. Trigger cancelled lines API for the account.

### UI Actions

- `Move Cancelled Lines`
- `Clone _D Docs`
- `Fix One-Time Payment Status`
- `Fix Prorate Packages`
- `Adjust Counters`
- `Reset billCalculated`
- `Update Payment Flags`
- `Generate Test Bill`
- `Delete Final Bill Doc`
- `Generate Final Bill`
- `Create Invoice Preview`
- `Revert Future Payment Flag`
- `Update 1001 Doc`
- `Remove schedulerReferenceId`
- `Trigger Cancelled Lines API`

## Scenario 3: Bill Generation With Active And Cancel Installments

### Flow

1. Check whether current month `3001_bsBillAcc::<accountId>` has active lines and cancelled lines.
2. If `cancelledInstallmentService` session is present:
   - if `cancelledInstallmentService.activeLineId` exists, update `3009_bsInstallment::<accountId>_<msisdn>_ins`
   - if `cancelledInstallmentService.activeLineId` does not exist, find a line called in the same bill month and move mapping from `cancelledInstallmentService.cancelledLineId` to `cancelledInstallmentService.activeLineId`
3. Decrement `recurringCharges.packages[].packageCurrentSlabCounter` by `1` for installment and installment-fee packages under `3009_bsInstallment`.
4. In current month `3006_bsUAD::<accountId>_<MMYY>`, change `payments[].paymentBilled` to `false` and ensure it is not present in previous month `2001` doc.
5. In next month `3006_bsUAD::<accountId>_<nextMMYY>`, change `payments[].paymentBilled` to `true` and ensure it is not present in current month `2001` doc.
6. Trigger test bill generation.
7. Delete final bill doc, validate with BU, then generate final bill without email.
8. Ask BU to validate final bill through invoice preview API.
9. Revert payment billed flag in next month `3006` doc back to `false`.
10. Update `1001` doc with future month payment.
11. If bill is generated after 26th of the month, remove `schedulerReferenceId` from billing account doc.

### UI Actions

- `Check Installment Session`
- `Update Installment Mapping`
- `Decrease Installment Counters`
- `Update Payment Flags`
- `Generate Test Bill`
- `Delete Final Bill Doc`
- `Generate Final Bill`
- `Create Invoice Preview`
- `Revert Future Payment Flag`
- `Update 1001 Doc`
- `Remove schedulerReferenceId`

## Basic UI Screen Proposal

### A. Header

- Screen title: `Billing Automation`
- environment selector: `DEV / UAT / PROD`
- operator name
- execution timestamp

### B. Scenario Form

Fields:

- Account ID
- Month-Year
- Bill Date
- Bill Due Date
- From Date
- To Date
- Bill Generation IP
- Bill Invoice IP
- Scenario Type

Buttons:

- `Load Scenario`
- `Run Pre-checks`

### C. Pre-checks Section

Show status cards for:

- Adjustment doc found
- Final bill already generated
- Test bill doc exists
- Cancelled lines present
- Installment session present
- Future month payment flag state

### D. Action Checklist

Each step should have:

- step name
- status: `Pending / Running / Done / Failed / Skipped`
- input data used
- action button
- result message

### E. API Payload Preview

Show editable JSON before trigger for:

- test bill generation
- final bill generation
- invoice validation
- cancelled lines API

### F. Audit Log

Record:

- who triggered the action
- when it was triggered
- request payload
- response
- affected document IDs

## Suggested Backend Responsibilities

The backend service behind the UI should provide APIs for:

- document lookup
- document backup
- document clone / rename
- selective field updates
- counter decrement with min-value guard
- payment flag toggle
- final bill existence check
- test bill trigger
- final bill trigger
- invoice preview trigger
- cancelled lines trigger
- audit logging

## Important Validation Rules

- Never decrease counters below allowed minimum.
- Always take backup before modifying billing documents.
- Final bill generation should stay disabled until BU validation is marked complete.
- When future month payment flag is changed temporarily, revert it after billing is completed.
- If final bill already exists and scenario requires regeneration, delete only the intended final bill document.
- Scheduler reference cleanup is required only when billing is done after the 26th day of the billing month.
- For `3006_bsUAD`, update `payments[].paymentBilled` and not a custom virtual field.
- For `3002_bsBillPlan` and `3009_bsInstallment`, update values inside `recurringCharges.packages[]`.
- For `2002_bsUBL`, reset `billCalculated` directly on the document.

## Open Points To Clarify Before Full Automation

- Exact source of truth for checking whether a document is "updated".
- Exact query method for document existence and backup.
- Exact format of `1001` document update for future month payment.
- Whether `_D` doc move means clone + delete, rename, or reinsert.
- Real Couchbase key format for cancelled-line `3002` and `3009` docs should be confirmed, because the sample file naming pattern does not fully match the JSON `id`.
- Exact package identification rules for installment and installment-fee packages.
- Whether BU validation should be manual checkbox only or integrated approval workflow.
- Whether billing APIs require pod exec, service DNS, or direct IP access.

## Recommended MVP

For the first version of the UI, implement only:

1. Scenario selection
2. input capture
3. pre-check status display
4. API payload preview
5. test bill trigger
6. final bill trigger
7. invoice validation trigger
8. operator checklist for manual doc changes
9. audit log

This will give the team a basic but usable UI before automating document edits fully.

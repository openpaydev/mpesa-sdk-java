package com.openpaydev.mpesa.backend.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Request model for STK Push transaction.
 */
@Getter
@Setter
@Builder
public class StkPushRequest {

    private final String phoneNumber;
    private final String amount;
    private final String accountReference;
    private final String transactionDesc;
    private final String callbackUrl;
    private final TransactionType transactionType;
    private final String partyB;

    public StkPushRequest(String phoneNumber, String amount, String accountReference,
                          String transactionDesc, String callbackUrl, TransactionType transactionType, String partyB) {
        this.phoneNumber = phoneNumber;
        this.amount = amount;
        this.accountReference = accountReference;
        this.transactionDesc = transactionDesc;
        this.callbackUrl = callbackUrl;
        this.transactionType = transactionType;
        this.partyB = partyB;
    }
}
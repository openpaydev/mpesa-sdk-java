package io.github.openpaydev.mpesa.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents the C2B transaction payload sent by M-Pesa to the confirmation and validation URLs.
 */
@Value
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class C2bTransaction {
    @JsonProperty("TransactionType")
    String transactionType;

    @JsonProperty("TransID")
    String transactionId;

    @JsonProperty("TransTime")
    String transactionTime;

    @JsonProperty("TransAmount")
    String transactionAmount;

    @JsonProperty("BusinessShortCode")
    String businessShortCode;

    @JsonProperty("BillRefNumber")
    String billRefNumber; // Account number

    @JsonProperty("InvoiceNumber")
    String invoiceNumber;

    @JsonProperty("OrgAccountBalance")
    String orgAccountBalance;

    @JsonProperty("ThirdPartyTransID")
    String thirdPartyTransID;

    @JsonProperty("MSISDN")
    String msisdn;

    @JsonProperty("FirstName")
    String firstName;

    @JsonProperty("MiddleName")
    String middleName;

    @JsonProperty("LastName")
    String lastName;
}
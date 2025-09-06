package io.github.openpaydev.mpesa.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.openpaydev.mpesa.backend.MpesaClient;
import lombok.*;

/**
 * Represents the complete request model for an M-Pesa STK Push API call.
 * <p>
 * This object is used to build a payment request. The recommended way to create an instance
 * is by using the static helper method {@link #newPayBillRequest(String, String, String, String, String)}.
 * <p>
 * The {@link MpesaClient} will automatically populate the
 * server-side fields ({@code BusinessShortCode}, {@code Password}, {@code Timestamp}, etc.) before sending the request.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StkPushRequest {

    @JsonProperty("BusinessShortCode")
    private String businessShortCode;

    @JsonProperty("Password")
    private String password;

    @JsonProperty("Timestamp")
    private String timestamp;

    @JsonProperty("TransactionType")
    private String transactionType;

    @JsonProperty("Amount")
    private String amount;

    @JsonProperty("PartyA")
    private String partyA;

    @JsonProperty("PartyB")
    private String partyB;

    @JsonProperty("PhoneNumber")
    private String phoneNumber;

    @JsonProperty("CallBackURL")
    private String callBackURL;

    @JsonProperty("AccountReference")
    private String accountReference;

    @JsonProperty("TransactionDesc")
    private String transactionDesc;

    /**
     * A convenient static factory method to create a standard PayBill STK Push request object.
     * <p>
     * This is the recommended way to create a new STK Push request.
     *
     * @param amount           The amount to be paid.
     * @param phoneNumber      The customer's phone number in MSISDN format (e.g., "254712345678").
     * @param accountReference Your internal reference for the transaction (e.g., "invoice-123").
     * @param transactionDesc  A short description of the payment (e.g., "Payment for shoes").
     * @param callBackURL      Your public HTTPS callback URL for receiving the final status.
     * @return A new, populated {@link StkPushRequest} instance.
     */
    public static StkPushRequest newPayBillRequest(String amount, String phoneNumber, String accountReference, String transactionDesc, String callBackURL) {
        return StkPushRequest.builder()
                .transactionType("CustomerPayBillOnline")
                .amount(amount)
                .partyA(phoneNumber)
                .phoneNumber(phoneNumber)
                .accountReference(accountReference)
                .transactionDesc(transactionDesc)
                .callBackURL(callBackURL)
                .build();
    }
}
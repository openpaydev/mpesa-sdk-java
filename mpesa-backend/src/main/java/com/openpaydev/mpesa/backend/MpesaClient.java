package com.openpaydev.mpesa.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openpaydev.mpesa.backend.auth.MpesaTokenManager;
import com.openpaydev.mpesa.backend.models.StkPushRequest;
import com.openpaydev.mpesa.backend.models.StkPushResponse;
import com.openpaydev.mpesa.backend.models.StkStatusQueryResponse;
import com.openpaydev.mpesa.core.MpesaConfig;
import okhttp3.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Client to interact with Mpesa APIs such as STK Push.
 */
public class MpesaClient {

    private final MpesaConfig config;
    private final MpesaTokenManager tokenManager;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MpesaClient(MpesaConfig config, MpesaTokenManager tokenManager, OkHttpClient client) {
        this.config = config;
        this.tokenManager = tokenManager;
        this.client = client;
    }

    /**
     * Performs an STK Push request.
     */
    public StkPushResponse stkPush(StkPushRequest request) throws IOException {
        String accessToken = tokenManager.getAccessToken();
        String timestamp = getTimestamp();
        String password = encodePassword(config.getShortCode(), config.getPassKey(), timestamp);
        String formattedPhoneNumber = formatPhoneNumber(request.getPhoneNumber());

        Map<String, Object> payload = new HashMap<>();
        payload.put("BusinessShortCode", config.getShortCode());
        payload.put("Password", password);
        payload.put("Timestamp", timestamp);
        payload.put("TransactionType", request.getTransactionType().name());
        payload.put("Amount", request.getAmount());
        payload.put("PartyA", formattedPhoneNumber);
        payload.put("PartyB", request.getPartyB() != null ? request.getPartyB() : config.getShortCode());
        payload.put("PhoneNumber", formattedPhoneNumber);
        payload.put("CallBackURL", request.getCallbackUrl());
        payload.put("AccountReference", request.getAccountReference());
        payload.put("TransactionDesc", request.getTransactionDesc());

        RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(payload),
                MediaType.get("application/json; charset=utf-8")
        );

        Request httpRequest = new Request.Builder()
                .url(config.getEnvironment().getBaseUrl() + "/mpesa/stkpush/v1/processrequest")
                .post(body)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();

            if (!response.isSuccessful()) {
                throw new IOException("STK Push failed with status: " + response.code() +
                        " and message: " + responseBody);
            }

            return objectMapper.readValue(responseBody, StkPushResponse.class);
        }
    }

    /**
     * Queries the status of an STK Push transaction.
     */
    public StkStatusQueryResponse queryStkStatus(String checkoutRequestID) throws IOException {
        String accessToken = tokenManager.getAccessToken();
        String timestamp = getTimestamp();
        String password = encodePassword(config.getShortCode(), config.getPassKey(), timestamp);

        Map<String, Object> payload = new HashMap<>();
        payload.put("BusinessShortCode", config.getShortCode());
        payload.put("Password", password);
        payload.put("Timestamp", timestamp);
        payload.put("CheckoutRequestID", checkoutRequestID);

        RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(payload),
                MediaType.get("application/json; charset=utf-8")
        );

        Request httpRequest = new Request.Builder()
                .url(config.getEnvironment().getBaseUrl() + "/mpesa/stkpushquery/v1/query")
                .post(body)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();

            if (!response.isSuccessful()) {
                throw new IOException("STK Push Query failed with status: " + response.code() +
                        " and message: " + responseBody);
            }

            return objectMapper.readValue(responseBody, StkStatusQueryResponse.class);
        }
    }

    private String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private String encodePassword(String shortCode, String passKey, String timestamp) {
        String strToEncode = shortCode + passKey + timestamp;
        return Base64.getEncoder().encodeToString(strToEncode.getBytes());
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty.");
        }

        if (phoneNumber.startsWith("+")) {
            phoneNumber = phoneNumber.substring(1);
        }

        if (phoneNumber.startsWith("07")) {
            return "254" + phoneNumber.substring(1);
        }

        if (phoneNumber.startsWith("7")) {
            return "254" + phoneNumber;
        }

        if (phoneNumber.startsWith("254")) {
            return phoneNumber;
        }

        throw new IllegalArgumentException("Invalid phone number format: " + phoneNumber);
    }

    public String getAccessToken() throws IOException {
        return tokenManager.getAccessToken();
    }
}

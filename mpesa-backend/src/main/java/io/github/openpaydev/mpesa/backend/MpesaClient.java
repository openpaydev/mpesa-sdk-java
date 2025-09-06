package io.github.openpaydev.mpesa.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.openpaydev.mpesa.backend.auth.MpesaTokenManager;
import io.github.openpaydev.mpesa.backend.models.StkPushRequest;
import io.github.openpaydev.mpesa.backend.models.StkPushResponse;
import io.github.openpaydev.mpesa.backend.models.StkStatusQueryRequest;
import io.github.openpaydev.mpesa.backend.models.StkStatusQueryResponse;
import io.github.openpaydev.mpesa.core.MpesaConfig;
import okhttp3.*;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Objects;

/**
 * The main client for interacting with the Safaricom M-Pesa API.
 * <p>
 * This client provides methods for performing key M-Pesa operations such as
 * initiating an STK Push and querying the status of a transaction.
 * It handles authentication, request signing, and communication with the API endpoints.
 * <p>
 * It is recommended to create a single instance of this client and reuse it throughout your application.
 * <p>
 * Example Usage:
 * <pre>{@code
 * // 1. Create a config and token manager
 * MpesaConfig config = MpesaConfig.fromEnv();
 * MpesaTokenManager tokenManager = new MpesaTokenManager(config);
 *
 * // 2. Create the MpesaClient (it's best to share one OkHttpClient in your app)
 * MpesaClient client = new MpesaClient(config, tokenManager, new OkHttpClient());
 *
 * // 3. Build a request for an STK Push
 * StkPushRequest request = StkPushRequest.userRequestBuilder(
 *      "10",                           // Amount
 *      "254712345678",                 // Phone Number
 *      "Test001",                      // Account Reference
 *      "Payment for goods",            // Transaction Description
 *      "https://example.com/callback"  // Callback URL
 * ).build();
 *
 * // 4. Make the API call
 * try {
 *     StkPushResponse response = client.stkPush(request);
 *     System.out.println("STK Push initiated successfully: " + response.getCheckoutRequestID());
 * } catch (IOException e) {
 *     System.err.println("Error initiating STK Push: " + e.getMessage());
 * }
 * }</pre>
 */
public class MpesaClient {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final ZoneId NAIROBI_ZONE_ID = ZoneId.of("Africa/Nairobi");

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
     * Initiates an M-Pesa STK Push (Lipa Na M-Pesa Online) request.
     * <p>
     * This method sends a payment prompt to the customer's phone. The customer then enters their
     * M-Pesa PIN to authorize the transaction. The result is sent to the specified callback URL.
     *
     * @param request An {@link StkPushRequest} object containing all the details for the transaction.
     *                Note that server-side fields (Password, Timestamp, etc.) will be populated by this method.
     * @return An {@link StkPushResponse} object with the initial synchronous response from the API.
     * @throws IOException If a network error occurs or the API returns a non-successful status code.
     */
    public StkPushResponse stkPush(StkPushRequest request) throws IOException {
        String accessToken = tokenManager.getAccessToken();
        String timestamp = getTimestamp();

        request.setBusinessShortCode(config.getBusinessShortCode());
        request.setPassword(encodePassword(config.getBusinessShortCode(), config.getPassKey(), timestamp));
        request.setTimestamp(timestamp);
        request.setPartyB(config.getBusinessShortCode()); // Use config shortcode as default PartyB
        request.setPartyA(formatPhoneNumber(request.getPartyA()));
        request.setPhoneNumber(formatPhoneNumber(request.getPhoneNumber()));

        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(request), JSON_MEDIA_TYPE);

        Request httpRequest = new Request.Builder()
                .url(config.getEnvironment().getStkPushUrl())
                .post(body)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            if (!response.isSuccessful()) {
                throw new IOException("STK Push failed. Status: " + response.code() + ", Body: " + responseBody);
            }
            return objectMapper.readValue(responseBody, StkPushResponse.class);
        }
    }

    /**
     * Queries the status of a pending or completed STK Push transaction.
     *
     * @param checkoutRequestID The unique ID of the STK Push transaction you want to query.
     *                          This ID is returned in the initial {@link StkPushResponse}.
     * @return An {@link StkStatusQueryResponse} object with the details of the transaction status.
     * @throws IOException If a network error occurs or the API returns a non-successful status code.
     */
    public StkStatusQueryResponse queryStkStatus(String checkoutRequestID) throws IOException {
        String accessToken = tokenManager.getAccessToken();
        String timestamp = getTimestamp();

        StkStatusQueryRequest queryRequest = new StkStatusQueryRequest(
                config.getBusinessShortCode(),
                encodePassword(config.getBusinessShortCode(), config.getPassKey(), timestamp),
                timestamp,
                checkoutRequestID
        );

        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(queryRequest), JSON_MEDIA_TYPE);

        Request httpRequest = new Request.Builder()
                .url(config.getEnvironment().getStkQueryUrl())
                .post(body)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            if (!response.isSuccessful()) {
                throw new IOException("STK Push Query failed. Status: " + response.code() + ", Body: " + responseBody);
            }
            return objectMapper.readValue(responseBody, StkStatusQueryResponse.class);
        }
    }


    private String getTimestamp() {
        return ZonedDateTime.now(NAIROBI_ZONE_ID).format(TIMESTAMP_FORMATTER);
    }

    private String encodePassword(String shortCode, String passKey, String timestamp) {
        String strToEncode = shortCode + passKey + timestamp;
        return Base64.getEncoder().encodeToString(strToEncode.getBytes());
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty.");
        }
        if (phoneNumber.startsWith("+")) phoneNumber = phoneNumber.substring(1);
        if (phoneNumber.startsWith("07")) return "254" + phoneNumber.substring(1);
        if (phoneNumber.startsWith("7")) return "254" + phoneNumber;
        if (phoneNumber.startsWith("254")) return phoneNumber;
        throw new IllegalArgumentException("Invalid phone number format: " + phoneNumber);
    }
}
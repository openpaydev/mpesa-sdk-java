package io.github.openpaydev.mpesa;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.openpaydev.mpesa.core.MpesaConfig;
import io.github.openpaydev.mpesa.core.MpesaService;
import io.github.openpaydev.mpesa.core.auth.TokenManager;
import io.github.openpaydev.mpesa.core.exceptions.MpesaApiException;
import io.github.openpaydev.mpesa.core.exceptions.MpesaException;
import io.github.openpaydev.mpesa.core.models.*;
import io.github.openpaydev.mpesa.core.utils.MpesaUtils;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

/**
 * The main client for interacting with the Safaricom M-Pesa API.
 * This class implements the {@link MpesaService} interface from mpesa-core.
 */
public class MpesaClient implements MpesaService {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private final MpesaConfig config;
    private final TokenManager tokenManager;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MpesaClient(MpesaConfig config, TokenManager tokenManager, OkHttpClient client) {
        this.config = config;
        this.tokenManager = tokenManager;
        this.client = client;
    }

    @Override
    public StkPushResponse stkPush(StkPushRequest userRequest) throws MpesaException {
        String timestamp = MpesaUtils.getTimestamp();
        String password = MpesaUtils.generatePassword(config.getBusinessShortCode(), config.getPassKey(), timestamp);

        // Use the toBuilder() method from Lombok to create a new, modified request object.
        // This avoids mutating the user's original object.
        StkPushRequest apiRequest = userRequest.toBuilder()
                .businessShortCode(config.getBusinessShortCode())
                .password(password)
                .timestamp(timestamp)
                .partyB(config.getBusinessShortCode())
                .partyA(MpesaUtils.formatPhoneNumber(userRequest.getPartyA()))
                .phoneNumber(MpesaUtils.formatPhoneNumber(userRequest.getPhoneNumber()))
                .build();

        return execute(config.getEnvironment().getStkPushUrl(), apiRequest, StkPushResponse.class);
    }

    @Override
    public StkStatusQueryResponse queryStkStatus(String checkoutRequestID) throws MpesaException {
        String timestamp = MpesaUtils.getTimestamp();
        String password = MpesaUtils.generatePassword(config.getBusinessShortCode(), config.getPassKey(), timestamp);

        StkStatusQueryRequest queryRequest = StkStatusQueryRequest.builder()
                .businessShortCode(config.getBusinessShortCode())
                .password(password)
                .timestamp(timestamp)
                .checkoutRequestID(checkoutRequestID)
                .build();

        return execute(config.getEnvironment().getStkQueryUrl(), queryRequest, StkStatusQueryResponse.class);
    }

    /**
     * A generic, private method to handle the boilerplate of executing authenticated HTTP POST requests.
     */
    private <T> T execute(String url, Object requestPayload, Class<T> responseClass) throws MpesaException {
        try {
            String accessToken = tokenManager.getAccessToken();
            RequestBody body = RequestBody.create(objectMapper.writeValueAsString(requestPayload), JSON_MEDIA_TYPE);

            Request httpRequest = new Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                String responseBody = Objects.requireNonNull(response.body()).string();
                if (!response.isSuccessful()) {
                    throw new MpesaApiException("API call failed", response.code(), responseBody);
                }
                return objectMapper.readValue(responseBody, responseClass);
            }
        } catch (IOException e) {
            throw new MpesaException("An unhandled network or parsing error occurred.", e);
        }
    }
}
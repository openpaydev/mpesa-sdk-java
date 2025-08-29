package com.openpaydev.mpesa.backend.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.openpaydev.mpesa.core.MpesaConfig;
import okhttp3.*;

import java.io.IOException;
import java.util.Base64;

/**
 * Manages OAuth access token retrieval and caching for Mpesa API.
 */
public class MpesaTokenManager {

    private final MpesaConfig config;
    private final OkHttpClient client;

    private String cachedToken;
    private long expiryTime = 0;

    /**
     * Quick setup — uses default OkHttpClient.
     */
    public MpesaTokenManager(MpesaConfig config) {
        this(config, new OkHttpClient());
    }

    /**
     * Power user constructor — allows custom OkHttpClient.
     */
    public MpesaTokenManager(MpesaConfig config, OkHttpClient client) {
        this.config = config;
        this.client = client;
    }

    /**
     * Factory for creating a manager from environment variables.
     */
    public static MpesaTokenManager fromDefaults() {
        MpesaConfig config = MpesaConfig.fromEnv();
        return new MpesaTokenManager(config);
    }

    /**
     * Returns a valid access token, either cached or freshly retrieved from Mpesa OAuth endpoint.
     *
     * @return OAuth access token string.
     * @throws IOException If an error occurs while fetching the token.
     */
    public String getAccessToken() throws IOException {
        if (cachedToken != null && System.currentTimeMillis() < expiryTime) {
            return cachedToken;
        }

        String credentials = config.getConsumerKey() + ":" + config.getConsumerSecret();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());

        Request request = new Request.Builder()
                .url(config.getEnvironment().getBaseUrl() + "/oauth/v1/generate?grant_type=client_credentials")
                .get()
                .header("Authorization", "Basic " + encoded)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get access token: " + response);
            }

            String body = response.body().string();
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            String token = json.get("access_token").getAsString();

            cachedToken = token;
            expiryTime = System.currentTimeMillis() + 3500 * 1000;

            return token;
        }
    }
}

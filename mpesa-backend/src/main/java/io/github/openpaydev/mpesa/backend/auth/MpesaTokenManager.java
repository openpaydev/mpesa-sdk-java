package io.github.openpaydev.mpesa.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.openpaydev.mpesa.backend.models.AccessTokenResponse;
import io.github.openpaydev.mpesa.core.MpesaConfig;
import okhttp3.*;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

/**
 * Manages OAuth access token retrieval and caching for the M-Pesa API.
 * <p>
 * This class ensures that a valid access token is available for API calls by
 * automatically refreshing it when it expires. It is designed to be thread-safe,
 * making it suitable for use in multi-threaded server environments.
 */
public class MpesaTokenManager {

    private static final long TOKEN_EXPIRY_BUFFER_MS = 60000;

    private final MpesaConfig config;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String cachedToken;
    private long expiryTime = 0;

    /**
     * Constructs a token manager with a default {@link OkHttpClient}.
     *
     * @param config The M-Pesa configuration object containing credentials.
     */
    public MpesaTokenManager(MpesaConfig config) {
        this(config, new OkHttpClient());
    }

    /**
     * Constructs a token manager with a custom {@link OkHttpClient}.
     * This is useful for sharing a single client instance across your application.
     *
     * @param config The M-Pesa configuration object containing credentials.
     * @param client The custom OkHttpClient to use for making requests.
     */
    public MpesaTokenManager(MpesaConfig config, OkHttpClient client) {
        this.config = config;
        this.client = client;
    }

    /**
     * A factory method to create a token manager using configuration from environment variables.
     * See {@link MpesaConfig#fromEnv()} for required variables.
     *
     * @return A new {@link MpesaTokenManager} instance.
     */
    public static MpesaTokenManager fromDefaults() {
        MpesaConfig config = MpesaConfig.fromEnv();
        return new MpesaTokenManager(config);
    }

    /**
     * Returns a valid access token, either from the cache or by fetching a new one.
     * <p>
     * This method is {@code synchronized} to prevent race conditions in multi-threaded environments,
     * ensuring that only one thread attempts to refresh an expired token at a time.
     *
     * @return A valid OAuth access token string.
     * @throws IOException If an error occurs while communicating with the M-Pesa API.
     */
    public synchronized String getAccessToken() throws IOException {
        if (cachedToken != null && System.currentTimeMillis() < (expiryTime - TOKEN_EXPIRY_BUFFER_MS)) {
            return cachedToken;
        }

        String credentials = config.getConsumerKey() + ":" + config.getConsumerSecret();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());

        Request request = new Request.Builder()
                .url(config.getEnvironment().getAuthUrl())
                .get()
                .header("Authorization", "Basic " + encoded)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get access token. Status: " + response.code() + ", Body: " + responseBody);
            }

            AccessTokenResponse tokenResponse = objectMapper.readValue(responseBody, AccessTokenResponse.class);

            this.cachedToken = tokenResponse.getAccessToken();
            this.expiryTime = System.currentTimeMillis() + (tokenResponse.getExpiresIn() * 1000);

            return this.cachedToken;
        }
    }
}
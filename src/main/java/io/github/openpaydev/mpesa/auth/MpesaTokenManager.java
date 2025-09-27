package io.github.openpaydev.mpesa.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.openpaydev.mpesa.core.MpesaConfig;
import io.github.openpaydev.mpesa.core.auth.TokenManager;
import io.github.openpaydev.mpesa.core.exceptions.MpesaAuthException;
import io.github.openpaydev.mpesa.core.models.AccessTokenResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.Clock; // Import the Clock class
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Concrete implementation of the {@link TokenManager} interface.
 * It retrieves and caches the M-Pesa OAuth access token using OkHttp.
 * This class is thread-safe.
 */
public class MpesaTokenManager implements TokenManager {

    private static final long TOKEN_EXPIRY_BUFFER_MS = 60000;

    private final MpesaConfig config;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Clock clock;

    private String cachedToken;
    private long expiryTime = 0;

    /**
     * The primary public constructor for the SDK.
     * It uses the default system clock.
     *
     * @param config The M-Pesa configuration object.
     * @param client The OkHttpClient for making requests.
     */
    public MpesaTokenManager(MpesaConfig config, OkHttpClient client) {
        this(config, client, Clock.systemDefaultZone());
    }

    /**
     * A package-private constructor for internal use and, crucially, for testing.
     * It allows a custom Clock to be injected.
     *
     * @param config The M-Pesa configuration object.
     * @param client The OkHttpClient for making requests.
     * @param clock  The Clock to use for time checks.
     */
    MpesaTokenManager(MpesaConfig config, OkHttpClient client, Clock clock) {
        this.config = config;
        this.client = client;
        this.clock = clock;
    }

    @Override
    public synchronized String getAccessToken() throws MpesaAuthException {
        if (cachedToken != null && clock.millis() < (expiryTime - TOKEN_EXPIRY_BUFFER_MS)) {
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
                throw new MpesaAuthException("Failed to get access token. Status: " + response.code() + ", Body: " + responseBody, null);
            }

            AccessTokenResponse tokenResponse = objectMapper.readValue(responseBody, AccessTokenResponse.class);

            this.cachedToken = tokenResponse.getAccessToken();

            long expiresInMillis = TimeUnit.SECONDS.toMillis(tokenResponse.getExpiresIn());
            this.expiryTime = clock.millis() + expiresInMillis;

            return this.cachedToken;
        } catch (IOException e) {
            throw new MpesaAuthException("Network error while fetching access token: " + e.getMessage(), e);
        }
    }
}
package io.github.openpaydev.mpesa.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.openpaydev.mpesa.core.MpesaConfig;
import io.github.openpaydev.mpesa.core.MpesaEnvironment;
import io.github.openpaydev.mpesa.core.exceptions.MpesaAuthException;
import io.github.openpaydev.mpesa.core.models.AccessTokenResponse;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MpesaTokenManagerTest {

    private MpesaTokenManager tokenManager;
    private MockWebServer mockWebServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MpesaConfig mpesaConfig;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        MpesaEnvironment mockEnvironment = mock(MpesaEnvironment.class);
        when(mockEnvironment.getAuthUrl()).thenReturn(mockWebServer.url("/").toString());
        when(mpesaConfig.getEnvironment()).thenReturn(mockEnvironment);

        when(mpesaConfig.getConsumerKey()).thenReturn("testKey");
        when(mpesaConfig.getConsumerSecret()).thenReturn("testSecret");

        tokenManager = new MpesaTokenManager(mpesaConfig, new OkHttpClient());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Should fetch token only once and return it to all threads under high contention")
    void getAccessToken_inMultiThreadedEnvironment_fetchesTokenOnlyOnce() throws Exception {
        AccessTokenResponse apiResponse = AccessTokenResponse.builder()
                .accessToken("threadSafeToken")
                .expiresIn(3599L)
                .build();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(apiResponse)));

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CyclicBarrier gate = new CyclicBarrier(threadCount + 1);

        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Future<String> future = executorService.submit(() -> {
                gate.await();
                return tokenManager.getAccessToken();
            });
            futures.add(future);
        }

        gate.await(5, TimeUnit.SECONDS);

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(1, mockWebServer.getRequestCount(),
                "API should only be called once by the first thread that acquires the lock.");

        assertDoesNotThrow(() -> {
            for (int i = 0; i < futures.size(); i++) {
                Future<String> future = futures.get(i);
                String token = future.get(1, TimeUnit.SECONDS);
                assertEquals("threadSafeToken", token, "Thread " + i + " should have received the correct token.");
            }
        }, "All threads should complete without exceptions and receive the token.");
    }

    @Test
    @DisplayName("Should throw MpesaAuthException on API error (e.g., 401)")
    void getAccessToken_whenApiReturnsError_throwsMpesaAuthException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"errorMessage\":\"Invalid credentials\"}"));

        MpesaAuthException exception = assertThrows(MpesaAuthException.class,
                () -> tokenManager.getAccessToken(),
                "Expected MpesaAuthException for non-successful API response."
        );

        assertTrue(exception.getMessage().contains("Failed to get access token. Status: 401"));
        assertNull(exception.getCause(), "Cause should be null for an API error, not a network error.");
    }

    /**
     * NEW TEST: This test simulates a complete network failure.
     * It verifies that the raw IOException from OkHttp is correctly caught and
     * wrapped inside our custom MpesaAuthException.
     */
    @Test
    @DisplayName("Should throw MpesaAuthException wrapping an IOException on network failure")
    void getAccessToken_whenNetworkFails_throwsMpesaAuthExceptionWrappingIOException() {
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        MpesaAuthException exception = assertThrows(MpesaAuthException.class,
                () -> tokenManager.getAccessToken(),
                "Expected MpesaAuthException for a network failure."
        );

        assertTrue(exception.getMessage().startsWith("Network error while fetching access token:"));
        assertNotNull(exception.getCause(), "The cause of the exception should be the original IOException.");
        assertInstanceOf(IOException.class, exception.getCause(), "The cause should be an instance of IOException.");
    }


    @Test
    @DisplayName("Should fetch a new token when cache is empty")
    void getAccessToken_whenNoToken_fetchesNewTokenSuccessfully() throws Exception {
        AccessTokenResponse apiResponse = AccessTokenResponse.builder().accessToken("aNewToken").expiresIn(3599L).build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(objectMapper.writeValueAsString(apiResponse)));

        String expectedCredentials = "testKey:testSecret";
        String expectedAuthHeader = "Basic " + Base64.getEncoder().encodeToString(expectedCredentials.getBytes(StandardCharsets.UTF_8));

        String token = tokenManager.getAccessToken();

        assertEquals("aNewToken", token);
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedAuthHeader, recordedRequest.getHeader("Authorization"));
    }

    @Test
    @DisplayName("Should return cached token if it is still valid")
    void getAccessToken_whenTokenIsValid_returnsCachedTokenWithoutApiCall() throws Exception {
        AccessTokenResponse apiResponse = AccessTokenResponse.builder().accessToken("aValidCachedToken").expiresIn(3599L).build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(objectMapper.writeValueAsString(apiResponse)));

        tokenManager.getAccessToken();
        assertEquals(1, mockWebServer.getRequestCount());
        tokenManager.getAccessToken();
        assertEquals(1, mockWebServer.getRequestCount());
    }

    @Test
    @DisplayName("Should fetch a new token when cached token is expired")
    void getAccessToken_whenTokenIsExpired_fetchesNewToken() throws Exception {
        AccessTokenResponse initialResponse = AccessTokenResponse.builder().accessToken("expiredToken").expiresIn(3600L).build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(objectMapper.writeValueAsString(initialResponse)));

        tokenManager.getAccessToken();
        assertEquals(1, mockWebServer.getRequestCount());

        Field expiryTimeField = MpesaTokenManager.class.getDeclaredField("expiryTime");
        expiryTimeField.setAccessible(true);
        expiryTimeField.set(tokenManager, System.currentTimeMillis() - 1);

        AccessTokenResponse refreshResponse = AccessTokenResponse.builder().accessToken("refreshedToken").expiresIn(3600L).build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(objectMapper.writeValueAsString(refreshResponse)));

        String refreshedToken = tokenManager.getAccessToken();

        assertEquals("refreshedToken", refreshedToken);
        assertEquals(2, mockWebServer.getRequestCount(), "API should be called a second time to refresh the token.");
    }
}
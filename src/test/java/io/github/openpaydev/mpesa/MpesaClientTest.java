package io.github.openpaydev.mpesa;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.openpaydev.mpesa.core.MpesaConfig;
import io.github.openpaydev.mpesa.core.MpesaEnvironment;
import io.github.openpaydev.mpesa.core.auth.TokenManager;
import io.github.openpaydev.mpesa.core.exceptions.MpesaApiException;
import io.github.openpaydev.mpesa.core.models.StkPushRequest;
import io.github.openpaydev.mpesa.core.models.StkPushResponse;
import io.github.openpaydev.mpesa.core.models.StkStatusQueryResponse;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MpesaClientTest {

    private MpesaClient mpesaClient;
    private MockWebServer mockWebServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MpesaConfig mpesaConfig;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private MpesaEnvironment mockEnvironment;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        when(mpesaConfig.getEnvironment()).thenReturn(mockEnvironment);

        when(mpesaConfig.getBusinessShortCode()).thenReturn("174379");
        when(mpesaConfig.getPassKey()).thenReturn("testPassKey");
        when(tokenManager.getAccessToken()).thenReturn("test-access-token");

        mpesaClient = new MpesaClient(mpesaConfig, tokenManager, new OkHttpClient());
    }

    @AfterEach
    void tearDown() throws java.io.IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("stkPush should successfully initiate a request and return a valid response")
    void stkPush_onSuccess_returnsStkPushResponse() throws Exception {
        when(mockEnvironment.getStkPushUrl()).thenReturn(mockWebServer.url("/mpesa/stkpush/v1/processrequest").toString());

        StkPushRequest userRequest = StkPushRequest.newPayBillRequest(
                "100", "254712345678", "TestRef001", "Test Payment", "https://test.callback.url/callback"
        );
        StkPushResponse apiResponse = StkPushResponse.builder()
                .merchantRequestID("MRID_12345").checkoutRequestID("CRID_67890").responseCode("0")
                .responseDescription("Success. Request accepted for processing")
                .customerMessage("Success. Request accepted for processing").build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(objectMapper.writeValueAsString(apiResponse)));

        StkPushResponse actualResponse = mpesaClient.stkPush(userRequest);

        assertNotNull(actualResponse);
        assertEquals("CRID_67890", actualResponse.getCheckoutRequestID());
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String jsonBody = recordedRequest.getBody().readUtf8();
        StkPushRequest sentRequest = objectMapper.readValue(jsonBody, StkPushRequest.class);
        assertEquals("174379", sentRequest.getBusinessShortCode());
    }

    @Test
    @DisplayName("queryStkStatus should construct the correct request and return a valid response")
    void queryStkStatus_onSuccess_returnsStatusResponse() throws Exception {
        when(mockEnvironment.getStkQueryUrl()).thenReturn(mockWebServer.url("/mpesa/stkpushquery/v1/query").toString());

        String checkoutRequestId = "CRID_67890";
        StkStatusQueryResponse apiResponse = StkStatusQueryResponse.builder()
                .responseCode("0").responseDescription("Accepted for processing").merchantRequestID("MRID_12345")
                .checkoutRequestID(checkoutRequestId).resultCode("0").resultDesc("The service request is processed successfully.").build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(objectMapper.writeValueAsString(apiResponse)));

        StkStatusQueryResponse actualResponse = mpesaClient.queryStkStatus(checkoutRequestId);

        assertNotNull(actualResponse);
        assertEquals("The service request is processed successfully.", actualResponse.getResultDesc());
        assertEquals(checkoutRequestId, actualResponse.getCheckoutRequestID());
    }

    @Test
    @DisplayName("stkPush should throw MpesaApiException when the API returns a non-200 status")
    void stkPush_whenApiReturnsError_throwsMpesaApiException() {
        when(mockEnvironment.getStkPushUrl()).thenReturn(mockWebServer.url("/mpesa/stkpush/v1/processrequest").toString());

        StkPushRequest userRequest = StkPushRequest.newPayBillRequest("100", "254712345678", "ref", "desc", "url");
        String errorBody = "{\"requestId\":\"err-1\",\"errorCode\":\"400.001\",\"errorMessage\":\"Bad Request\"}";
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody(errorBody));

        MpesaApiException exception = assertThrows(MpesaApiException.class, () -> mpesaClient.stkPush(userRequest));
        assertEquals(400, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("API call failed"));
    }
}
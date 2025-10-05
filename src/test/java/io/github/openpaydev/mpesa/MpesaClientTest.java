package io.github.openpaydev.mpesa;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.openpaydev.mpesa.core.MpesaConfig;
import io.github.openpaydev.mpesa.core.MpesaEnvironment;
import io.github.openpaydev.mpesa.core.auth.TokenManager;
import io.github.openpaydev.mpesa.core.exceptions.MpesaApiException;
import io.github.openpaydev.mpesa.core.models.*;
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
        when(mpesaConfig.getPassKey()).thenReturn("testPassKey");

        StkPushRequest userRequest = StkPushRequest.newPayBillRequest("100", "254712345678", "ref", "desc", "url");
        StkPushResponse apiResponse = StkPushResponse.builder().checkoutRequestID("CRID_67890").responseCode("0").build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(objectMapper.writeValueAsString(apiResponse)));

        StkPushResponse actualResponse = mpesaClient.stkPush(userRequest);

        assertNotNull(actualResponse);
        assertEquals("CRID_67890", actualResponse.getCheckoutRequestID());
    }

    @Test
    @DisplayName("queryStkStatus should construct the correct request and return a valid response")
    void queryStkStatus_onSuccess_returnsStatusResponse() throws Exception {
        when(mockEnvironment.getStkQueryUrl()).thenReturn(mockWebServer.url("/mpesa/stkpushquery/v1/query").toString());
        when(mpesaConfig.getPassKey()).thenReturn("testPassKey");

        String checkoutRequestId = "CRID_67890";
        StkStatusQueryResponse apiResponse = StkStatusQueryResponse.builder().resultDesc("Success").build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(objectMapper.writeValueAsString(apiResponse)));

        StkStatusQueryResponse actualResponse = mpesaClient.queryStkStatus(checkoutRequestId);

        assertNotNull(actualResponse);
        assertEquals("Success", actualResponse.getResultDesc());
    }

    @Test
    @DisplayName("stkPush should throw MpesaApiException when the API returns a non-200 status")
    void stkPush_whenApiReturnsError_throwsMpesaApiException() {
        when(mockEnvironment.getStkPushUrl()).thenReturn(mockWebServer.url("/mpesa/stkpush/v1/processrequest").toString());
        when(mpesaConfig.getPassKey()).thenReturn("testPassKey");

        StkPushRequest userRequest = StkPushRequest.newPayBillRequest("100", "254712345678", "ref", "desc", "url");
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("{}"));

        assertThrows(MpesaApiException.class, () -> mpesaClient.stkPush(userRequest));
    }

    @Test
    @DisplayName("registerC2bUrl should construct the correct request and return a valid response")
    void registerC2bUrl_onSuccess_returnsValidResponse() throws Exception {
        when(mockEnvironment.getC2bRegisterUrl()).thenReturn(mockWebServer.url("/mpesa/c2b/v1/registerurl").toString());

        C2bRegisterUrlRequest userRequest = C2bRegisterUrlRequest.builder()
                .responseType(C2bResponseType.Completed)
                .confirmationUrl("https://a.com").validationUrl("https://b.com").build();

        C2bRegisterUrlResponse apiResponse = C2bRegisterUrlResponse.builder().responseDescription("success").build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(objectMapper.writeValueAsString(apiResponse)));

        C2bRegisterUrlResponse actualResponse = mpesaClient.registerC2bUrl(userRequest);

        assertNotNull(actualResponse);
        assertEquals("success", actualResponse.getResponseDescription());
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String jsonBody = recordedRequest.getBody().readUtf8();
        C2bRegisterUrlRequest sentRequest = objectMapper.readValue(jsonBody, C2bRegisterUrlRequest.class);
        assertEquals("174379", sentRequest.getShortCode());
    }
}
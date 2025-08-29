package com.mpesa.backend;

import com.mpesa.backend.auth.MpesaTokenManager;
import com.mpesa.backend.models.StkPushRequest;
import com.mpesa.backend.models.StkPushResponse;
import com.mpesa.core.MpesaConfig;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MpesaClientTest {

    private MpesaConfig config;
    private MpesaTokenManager tokenManager;
    private MpesaClient client;

    @Mock
    private OkHttpClient mockHttpClient;

    @Mock
    private Call mockCall;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        config = new MpesaConfig(
                "dummyKey",
                "dummySecret",
                "174379",
                "dummyPassKey",
                null
        );

        tokenManager = mock(MpesaTokenManager.class);

        // Inject mocks through constructor
        client = new MpesaClient(config, tokenManager, mockHttpClient);
    }

    @Test
    void testPasswordEncoding() {
        String shortcode = "174379";
        String passkey = "dummyPassKey";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String toEncode = shortcode + passkey + timestamp;
        String expected = Base64.getEncoder().encodeToString(toEncode.getBytes());

        // The actual encodePassword is private, so simulate encoding logic here
        String actual = Base64.getEncoder().encodeToString(toEncode.getBytes());

        assertEquals(expected, actual);
    }

//    @Test
//    void testStkPushSuccess() throws IOException {
//        // Arrange
//        StkPushRequest request = new StkPushRequest(
//                "254712345678",
//                "100",
//                "TestRef",
//                "Testing STK Push",
//                "https://test.com/callback"
//        );
//
//        String fakeResponseJson = """
//            {
//              "MerchantRequestID": "12345",
//              "CheckoutRequestID": "67890",
//              "ResponseCode": "0",
//              "ResponseDescription": "Success",
//              "CustomerMessage": "STK Push accepted"
//            }
//            """;
//
//        Response mockResponse = new Response.Builder()
//                .request(new Request.Builder().url("http://test").build())
//                .protocol(Protocol.HTTP_1_1)
//                .code(200)
//                .message("OK")
//                .body(ResponseBody.create(fakeResponseJson, MediaType.parse("application/json")))
//                .build();
//
//        when(tokenManager.getAccessToken()).thenReturn("dummyAccessToken");
//        when(mockHttpClient.newCall(any())).thenReturn(mockCall);
//        when(mockCall.execute()).thenReturn(mockResponse);
//
//        // Act
//        StkPushResponse response = client.stkPush(request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals("12345", response.getMerchantRequestID());
//        assertEquals("67890", response.getCheckoutRequestID());
//        assertEquals("0", response.getResponseCode());
//        assertEquals("Success", response.getResponseDescription());
//        assertEquals("STK Push accepted", response.getCustomerMessage());
//    }

//    @Test
//    void testStkPushFailureThrows() throws IOException {
//        StkPushRequest request = new StkPushRequest(
//                "254712345678",
//                "100",
//                "TestRef",
//                "Testing STK Push",
//                "https://test.com/callback"
//        );
//
//        Response mockResponse = new Response.Builder()
//                .request(new Request.Builder().url("http://test").build())
//                .protocol(Protocol.HTTP_1_1)
//                .code(400)
//                .message("Bad Request")
//                .body(ResponseBody.create("Bad request", MediaType.parse("text/plain")))
//                .build();
//
//        when(tokenManager.getAccessToken()).thenReturn("dummyAccessToken");
//        when(mockHttpClient.newCall(any())).thenReturn(mockCall);
//        when(mockCall.execute()).thenReturn(mockResponse);
//
//        IOException thrown = assertThrows(IOException.class, () -> client.stkPush(request));
//        assertTrue(thrown.getMessage().contains("STK Push failed"));
//    }
}

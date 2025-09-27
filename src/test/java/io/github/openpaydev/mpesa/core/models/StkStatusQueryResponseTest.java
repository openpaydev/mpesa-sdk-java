package io.github.openpaydev.mpesa.core.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StkStatusQueryResponseTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should deserialize a successful transaction query response from JSON")
    void shouldDeserializeSuccessfulResponse() throws JsonProcessingException {
        String successfulJson = new StringBuilder().append("{\n").append("    \"ResponseCode\": \"0\",\n").append("    \"ResponseDescription\": \"The service request has been accepted successfully\",\n").append("    \"MerchantRequestID\": \"12345-67890-1\",\n").append("    \"CheckoutRequestID\": \"ws_CO_060920251234567890\",\n").append("    \"ResultCode\": \"0\",\n").append("    \"ResultDesc\": \"The service request is processed successfully.\"\n").append("}").toString();

        StkStatusQueryResponse response = objectMapper.readValue(successfulJson, StkStatusQueryResponse.class);

        assertNotNull(response);
        assertEquals("0", response.getResponseCode());
        assertEquals("12345-67890-1", response.getMerchantRequestID());
        assertEquals("0", response.getResultCode());
    }

    @Test
    @DisplayName("Should deserialize a failed transaction query response from JSON")
    void shouldDeserializeFailedResponse() throws JsonProcessingException {
        String failedJson = new StringBuilder().append("{\n").append("    \"ResponseCode\": \"0\",\n").append("    \"ResponseDescription\": \"The service request has been accepted successfully\",\n").append("    \"MerchantRequestID\": \"54321-09876-1\",\n").append("    \"CheckoutRequestID\": \"ws_CO_060920250987654321\",\n").append("    \"ResultCode\": \"1032\",\n").append("    \"ResultDesc\": \"Request cancelled by user.\"\n").append("}").toString();

        StkStatusQueryResponse response = objectMapper.readValue(failedJson, StkStatusQueryResponse.class);

        assertNotNull(response);
        assertEquals("1032", response.getResultCode());
    }

    @Test
    @DisplayName("ToString should contain field values")
    void toStringShouldContainFieldValues() {
        StkStatusQueryResponse response = StkStatusQueryResponse.builder()
                .merchantRequestID("merchant-1")
                .checkoutRequestID("checkout-1")
                .resultCode("0")
                .resultDesc("Success")
                .build();

        String stringRepresentation = response.toString();

        assertTrue(stringRepresentation.contains("merchantRequestID=merchant-1"));
        assertTrue(stringRepresentation.contains("resultCode=0"));
        assertTrue(stringRepresentation.contains("resultDesc=Success"));
    }

    @Test
    @DisplayName("Should deserialize gracefully when extra JSON fields are present")
    void shouldDeserializeWithExtraFields() throws JsonProcessingException {
        String jsonWithExtraField = new StringBuilder().append("{\n").append("    \"ResponseCode\": \"0\",\n").append("    \"ResponseDescription\": \"Success\",\n").append("    \"MerchantRequestID\": \"12345\",\n").append("    \"CheckoutRequestID\": \"ws_CO_12345\",\n").append("    \"ResultCode\": \"0\",\n").append("    \"ResultDesc\": \"Success\",\n").append("    \"NewFutureField\": \"some-value\"\n").append("}").toString();

        StkStatusQueryResponse response = assertDoesNotThrow(() ->
                objectMapper.readValue(jsonWithExtraField, StkStatusQueryResponse.class)
        );

        assertEquals("0", response.getResultCode());
        assertEquals("12345", response.getMerchantRequestID());
    }
}
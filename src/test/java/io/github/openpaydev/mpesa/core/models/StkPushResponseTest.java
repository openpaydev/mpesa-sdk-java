package io.github.openpaydev.mpesa.core.models;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StkPushResponseTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @DisplayName("Should deserialize a successful STK push response from JSON")
  void shouldDeserializeSuccessfulResponse() throws JsonProcessingException {
    String successfulJson =
        new StringBuilder()
            .append("{\n")
            .append("    \"MerchantRequestID\": \"12345-67890-1\",\n")
            .append("    \"CheckoutRequestID\": \"ws_CO_060920251234567890\",\n")
            .append("    \"ResponseCode\": \"0\",\n")
            .append("    \"ResponseDescription\": \"Success. Request accepted for processing\",\n")
            .append("    \"CustomerMessage\": \"Success. Request accepted for processing\"\n")
            .append("}")
            .toString();

    StkPushResponse response = objectMapper.readValue(successfulJson, StkPushResponse.class);

    assertNotNull(response);
    assertEquals("12345-67890-1", response.getMerchantRequestID());
    assertEquals("ws_CO_060920251234567890", response.getCheckoutRequestID());
    assertEquals("0", response.getResponseCode());
    assertEquals("Success. Request accepted for processing", response.getResponseDescription());
    assertEquals("Success. Request accepted for processing", response.getCustomerMessage());
  }

  @Test
  @DisplayName("Should deserialize an error STK push response from JSON")
  void shouldDeserializeErrorResponse() throws JsonProcessingException {
    String errorJson =
        new StringBuilder()
            .append("{\n")
            .append("    \"MerchantRequestID\": \"98765-43210-1\",\n")
            .append("    \"CheckoutRequestID\": \"ws_CO_060920259876543210\",\n")
            .append("    \"ResponseCode\": \"1032\",\n")
            .append("    \"ResponseDescription\": \"Request cancelled by user.\",\n")
            .append("    \"CustomerMessage\": \"Request cancelled by user.\"\n")
            .append("}")
            .toString();

    StkPushResponse response = objectMapper.readValue(errorJson, StkPushResponse.class);

    assertNotNull(response);
    assertEquals("98765-43210-1", response.getMerchantRequestID());
    assertEquals("1032", response.getResponseCode());
    assertEquals("Request cancelled by user.", response.getResponseDescription());
  }

  @Test
  @DisplayName("ToString should contain field values")
  void toStringShouldContainFieldValues() {
    StkPushResponse response =
        StkPushResponse.builder()
            .merchantRequestID("merchant-1")
            .checkoutRequestID("checkout-1")
            .responseCode("0")
            .build();

    String stringRepresentation = response.toString();

    assertTrue(stringRepresentation.contains("merchantRequestID=merchant-1"));
    assertTrue(stringRepresentation.contains("checkoutRequestID=checkout-1"));
    assertTrue(stringRepresentation.contains("responseCode=0"));
  }
}

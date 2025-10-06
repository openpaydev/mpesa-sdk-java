package io.github.openpaydev.mpesa.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StkStatusQueryRequestTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("Builder should initialize all fields correctly")
  void builder_andGetters_shouldWorkCorrectly() {
    String shortCode = "174379";
    String password = "base64EncodedPassword";
    String timestamp = "20250906113000";
    String checkoutId = "ws_CO_060920251234567890";
    StkStatusQueryRequest request =
        StkStatusQueryRequest.builder()
            .businessShortCode(shortCode)
            .password(password)
            .timestamp(timestamp)
            .checkoutRequestID(checkoutId)
            .build();

    assertEquals(shortCode, request.getBusinessShortCode());
    assertEquals(password, request.getPassword());
    assertEquals(timestamp, request.getTimestamp());
    assertEquals(checkoutId, request.getCheckoutRequestID());
  }

  @Test
  @DisplayName("ToString should contain the field values")
  void toString_shouldContainFieldValues() {
    StkStatusQueryRequest request =
        StkStatusQueryRequest.builder()
            .businessShortCode("174379")
            .password("aPassword")
            .timestamp("aTimestamp")
            .checkoutRequestID("aCheckoutId")
            .build();

    String stringRepresentation = request.toString();

    assertTrue(
        stringRepresentation.contains("businessShortCode=174379"),
        "ToString should include BusinessShortCode.");
    assertTrue(
        stringRepresentation.contains("checkoutRequestID=aCheckoutId"),
        "ToString should include CheckoutRequestID.");
  }

  @Test
  @DisplayName("Should serialize to JSON with correct property names")
  void shouldSerializeToJsonWithCorrectPropertyNames() throws JsonProcessingException {
    String shortCode = "174379";
    String password = "base64EncodedPassword";
    String timestamp = "20250906113000";
    String checkoutId = "ws_CO_060920251234567890";
    StkStatusQueryRequest request =
        StkStatusQueryRequest.builder()
            .businessShortCode(shortCode)
            .password(password)
            .timestamp(timestamp)
            .checkoutRequestID(checkoutId)
            .build();

    String jsonString = objectMapper.writeValueAsString(request);
    JsonNode jsonNode = objectMapper.readTree(jsonString);

    assertTrue(jsonNode.has("BusinessShortCode"), "JSON key 'BusinessShortCode' should exist.");
    assertTrue(jsonNode.has("Password"), "JSON key 'Password' should exist.");
    assertTrue(jsonNode.has("Timestamp"), "JSON key 'Timestamp' should exist.");
    assertTrue(jsonNode.has("CheckoutRequestID"), "JSON key 'CheckoutRequestID' should exist.");

    assertEquals(shortCode, jsonNode.get("BusinessShortCode").asText());
    assertEquals(password, jsonNode.get("Password").asText());
    assertEquals(timestamp, jsonNode.get("Timestamp").asText());
    assertEquals(checkoutId, jsonNode.get("CheckoutRequestID").asText());
  }
}

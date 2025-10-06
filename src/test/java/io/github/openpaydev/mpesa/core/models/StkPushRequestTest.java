package io.github.openpaydev.mpesa.core.models;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StkPushRequestTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("newPayBillRequest factory should create a correctly populated object")
  void newPayBillRequest_shouldPopulateFieldsCorrectly() {
    String amount = "10";
    String phoneNumber = "254712345678";
    String accountReference = "TestRef001";
    String transactionDesc = "Payment for goods";
    String callBackURL = "https://example.com/callback";

    StkPushRequest request =
        StkPushRequest.newPayBillRequest(
            amount, phoneNumber, accountReference, transactionDesc, callBackURL);

    assertNotNull(request);
    assertEquals("CustomerPayBillOnline", request.getTransactionType());
    assertEquals(amount, request.getAmount());
    assertEquals(phoneNumber, request.getPhoneNumber());
    assertEquals(phoneNumber, request.getPartyA());
    assertEquals(accountReference, request.getAccountReference());
    assertEquals(transactionDesc, request.getTransactionDesc());
    assertEquals(callBackURL, request.getCallBackURL());

    assertNull(request.getBusinessShortCode());
    assertNull(request.getPassword());
    assertNull(request.getPartyB());
  }

  @Test
  @DisplayName("Builder should create a complete StkPushRequest object")
  void builder_shouldCreateCompleteObject() {
    String shortCode = "174379";
    String password = "testPassword";
    String timestamp = "20250906100000";
    String partyB = "174379";

    StkPushRequest request =
        StkPushRequest.builder()
            .businessShortCode(shortCode)
            .password(password)
            .timestamp(timestamp)
            .transactionType("CustomerPayBillOnline")
            .amount("1")
            .partyA("254712345678")
            .partyB(partyB)
            .phoneNumber("254712345678")
            .callBackURL("https://example.com/callback")
            .accountReference("INV-001")
            .transactionDesc("Test Payment")
            .build();

    assertEquals(shortCode, request.getBusinessShortCode());
    assertEquals(password, request.getPassword());
    assertEquals(timestamp, request.getTimestamp());
    assertEquals(partyB, request.getPartyB());
    assertEquals("INV-001", request.getAccountReference());
  }

  @Test
  @DisplayName("toBuilder() should create a modified copy of an existing object")
  void toBuilder_shouldCreateModifiedCopy() {
    StkPushRequest initialRequest =
        StkPushRequest.newPayBillRequest(
            "10", "254712345678", "Invoice-123", "Payment", "https://my.service.com/callback");

    StkPushRequest finalRequest =
        initialRequest.toBuilder()
            .businessShortCode("174379")
            .partyB("174379")
            .password("generated-password")
            .timestamp("generated-timestamp")
            .build();

    assertEquals("10", finalRequest.getAmount());
    assertEquals("254712345678", finalRequest.getPhoneNumber());
    assertEquals("174379", finalRequest.getBusinessShortCode());
    assertEquals("generated-password", finalRequest.getPassword());
  }

  @Test
  @DisplayName("ToString should contain key field values")
  void toString_shouldContainFieldValues() {
    StkPushRequest request =
        StkPushRequest.newPayBillRequest("1", "254700000000", "AccRef", "Desc", "https://url.com");

    String stringRepresentation = request.toString();

    assertTrue(stringRepresentation.contains("amount=1"));
    assertTrue(stringRepresentation.contains("accountReference=AccRef"));
    assertTrue(stringRepresentation.contains("phoneNumber=254700000000"));
  }

  @Test
  @DisplayName("Should serialize to JSON with correct property names")
  void shouldSerializeToJsonWithCorrectPropertyNames() throws JsonProcessingException {
    StkPushRequest request =
        StkPushRequest.builder()
            .businessShortCode("174379")
            .transactionType("CustomerPayBillOnline")
            .amount("10")
            .partyA("254712345678")
            .phoneNumber("254712345678")
            .callBackURL("https://my.service.com/callback")
            .accountReference("Invoice-123")
            .transactionDesc("Payment")
            .build();

    String jsonString = objectMapper.writeValueAsString(request);
    JsonNode jsonNode = objectMapper.readTree(jsonString);

    assertTrue(jsonNode.has("BusinessShortCode"), "JSON should have 'BusinessShortCode' field.");
    assertTrue(jsonNode.has("CallBackURL"), "JSON should have 'CallBackURL' field.");
    assertFalse(
        jsonNode.has("password"), "Password field should be null and not serialized if not set.");

    assertEquals("174379", jsonNode.get("BusinessShortCode").asText());
    assertEquals("10", jsonNode.get("Amount").asText());
    assertEquals("https://my.service.com/callback", jsonNode.get("CallBackURL").asText());
  }
}

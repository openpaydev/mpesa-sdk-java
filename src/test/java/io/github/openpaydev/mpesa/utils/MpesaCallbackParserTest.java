package io.github.openpaydev.mpesa.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.openpaydev.mpesa.core.models.StkCallback;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MpesaCallbackParserTest {

  @Test
  @DisplayName("Should parse a successful transaction callback JSON correctly")
  void parse_shouldSucceed_forSuccessfulTransaction() throws JsonProcessingException {
    String successJson =
        new StringBuilder()
            .append("{\n")
            .append("  \"Body\": {\n")
            .append("    \"stkCallback\": {\n")
            .append("      \"MerchantRequestID\": \"12345-67890-1\",\n")
            .append("      \"CheckoutRequestID\": \"ws_CO_0123456789_ABCDEF\",\n")
            .append("      \"ResultCode\": 0,\n")
            .append("      \"ResultDesc\": \"The service request is processed successfully.\",\n")
            .append("      \"CallbackMetadata\": {\n")
            .append("        \"Item\": [\n")
            .append("          {\n")
            .append("            \"Name\": \"Amount\",\n")
            .append("            \"Value\": 1.00\n")
            .append("          },\n")
            .append("          {\n")
            .append("            \"Name\": \"MpesaReceiptNumber\",\n")
            .append("            \"Value\": \"QWERTY12345\"\n")
            .append("          },\n")
            .append("          {\n")
            .append("            \"Name\": \"TransactionDate\",\n")
            .append("            \"Value\": 20250906123045\n")
            .append("          },\n")
            .append("          {\n")
            .append("            \"Name\": \"PhoneNumber\",\n")
            .append("            \"Value\": 254712345678\n")
            .append("          }\n")
            .append("        ]\n")
            .append("      }\n")
            .append("    }\n")
            .append("  }\n")
            .append("}")
            .toString();

    StkCallback callback = MpesaCallbackParser.parse(successJson);

    assertNotNull(callback, "The parsed callback object should not be null.");
    assertNotNull(callback.getBody(), "The 'Body' field should not be null.");
    StkCallback.StkCallbackData stkCallbackData = callback.getBody().getStkCallback();
    assertNotNull(stkCallbackData, "The 'stkCallback' data should not be null.");

    assertEquals(0, stkCallbackData.getResultCode());
    assertEquals("The service request is processed successfully.", stkCallbackData.getResultDesc());
    assertEquals("12345-67890-1", stkCallbackData.getMerchantRequestID());
    assertEquals("ws_CO_0123456789_ABCDEF", stkCallbackData.getCheckoutRequestID());

    StkCallback.CallbackMetadata metadata = stkCallbackData.getCallbackMetadata();
    assertNotNull(metadata, "CallbackMetadata should be present for successful transactions.");
    assertNotNull(metadata.getItems(), "Metadata 'Item' array should not be null.");
    assertEquals(4, metadata.getItems().length, "There should be 4 items in the metadata array.");

    StkCallback.CallbackItem amountItem = metadata.getItems()[0];
    assertEquals("Amount", amountItem.getName());
    assertInstanceOf(
        Double.class, amountItem.getValue(), "Amount should be deserialized as Double.");
    assertEquals(1.00, amountItem.getValue());

    StkCallback.CallbackItem receiptItem = metadata.getItems()[1];
    assertEquals("MpesaReceiptNumber", receiptItem.getName());
    assertInstanceOf(String.class, receiptItem.getValue(), "Receipt Number should be a String.");
    assertEquals("QWERTY12345", receiptItem.getValue());

    StkCallback.CallbackItem dateItem = metadata.getItems()[2];
    assertEquals("TransactionDate", dateItem.getName());
    assertInstanceOf(
        Long.class, dateItem.getValue(), "Transaction Date should be deserialized as Long.");
    assertEquals(20250906123045L, dateItem.getValue());

    StkCallback.CallbackItem phoneItem = metadata.getItems()[3];
    assertEquals("PhoneNumber", phoneItem.getName());
    assertInstanceOf(
        Long.class, phoneItem.getValue(), "Phone Number should be deserialized as Long.");
    assertEquals(254712345678L, phoneItem.getValue());
  }

  @Test
  @DisplayName("Should parse a failed transaction callback JSON correctly")
  void parse_shouldSucceed_forFailedTransaction() throws JsonProcessingException {
    String cancelledJson =
        new StringBuilder()
            .append("{\n")
            .append("  \"Body\": {\n")
            .append("    \"stkCallback\": {\n")
            .append("      \"MerchantRequestID\": \"54321-09876-1\",\n")
            .append("      \"CheckoutRequestID\": \"ws_CO_9876543210_ZYXWV\",\n")
            .append("      \"ResultCode\": 1032,\n")
            .append("      \"ResultDesc\": \"Request cancelled by user.\"\n")
            .append("    }\n")
            .append("  }\n")
            .append("}")
            .toString();

    StkCallback callback = MpesaCallbackParser.parse(cancelledJson);

    assertNotNull(callback);
    StkCallback.StkCallbackData stkCallbackData = callback.getBody().getStkCallback();
    assertNotNull(stkCallbackData);

    assertEquals(1032, stkCallbackData.getResultCode());
    assertEquals("Request cancelled by user.", stkCallbackData.getResultDesc());
    assertEquals("54321-09876-1", stkCallbackData.getMerchantRequestID());
    assertNull(
        stkCallbackData.getCallbackMetadata(),
        "CallbackMetadata should be null for failed transactions.");
  }

  @Test
  @DisplayName("Should throw JsonProcessingException for malformed JSON")
  void parse_shouldThrowException_forMalformedJson() {
    String malformedJson =
        new StringBuilder()
            .append("{\n")
            .append("  \"Body\": {\n")
            .append("    \"stkCallback\": {\n")
            .append("      \"ResultCode\": 0,\n")
            .append("    }\n")
            .append("  }\n")
            .append("}")
            .toString();

    assertThrows(JsonProcessingException.class, () -> MpesaCallbackParser.parse(malformedJson));
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException for null input")
  void parse_shouldThrowIllegalArgumentException_forNullInput() {
    assertThrows(IllegalArgumentException.class, () -> MpesaCallbackParser.parse(null));
  }

  @Test
  @DisplayName("Should throw JsonProcessingException for empty string input")
  void parse_shouldThrowJsonProcessingException_forEmptyInput() {
    assertThrows(JsonProcessingException.class, () -> MpesaCallbackParser.parse(""));
  }

  @Test
  @DisplayName("Should not be able to instantiate the utility class")
  void constructor_shouldBePrivateAndThrowException() throws NoSuchMethodException {
    Constructor<MpesaCallbackParser> constructor =
        MpesaCallbackParser.class.getDeclaredConstructor();
    assertTrue(
        java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()),
        "Constructor must be private.");
    constructor.setAccessible(true);

    assertThrows(InvocationTargetException.class, constructor::newInstance);
  }
}

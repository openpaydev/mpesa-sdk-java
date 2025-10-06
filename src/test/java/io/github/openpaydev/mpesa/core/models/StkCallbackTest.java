package io.github.openpaydev.mpesa.core.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StkCallbackTest {

  @Test
  @DisplayName("Should construct a full, nested StkCallback object graph using builders")
  void shouldConstructFullObjectGraph() {
    StkCallback.CallbackItem amountItem =
        StkCallback.CallbackItem.builder().name("Amount").value(1.0).build();

    StkCallback.CallbackItem receiptItem =
        StkCallback.CallbackItem.builder().name("MpesaReceiptNumber").value("ABC123XYZ").build();

    StkCallback.CallbackMetadata metadata =
        StkCallback.CallbackMetadata.builder()
            .items(new StkCallback.CallbackItem[] {amountItem, receiptItem})
            .build();

    StkCallback.StkCallbackData callbackData =
        StkCallback.StkCallbackData.builder()
            .merchantRequestID("merchant-id-1")
            .checkoutRequestID("checkout-id-2")
            .resultCode(0)
            .resultDesc("Success")
            .callbackMetadata(metadata)
            .build();

    StkCallback.Body body = StkCallback.Body.builder().stkCallback(callbackData).build();

    StkCallback stkCallback = StkCallback.builder().body(body).build();

    assertNotNull(stkCallback.getBody());
    assertEquals(callbackData, stkCallback.getBody().getStkCallback());
    assertEquals(0, stkCallback.getBody().getStkCallback().getResultCode());

    StkCallback.CallbackMetadata retrievedMetadata =
        stkCallback.getBody().getStkCallback().getCallbackMetadata();
    assertNotNull(retrievedMetadata);
    assertEquals(2, retrievedMetadata.getItems().length);

    assertEquals("MpesaReceiptNumber", retrievedMetadata.getItems()[1].getName());
    assertEquals("ABC123XYZ", retrievedMetadata.getItems()[1].getValue());
  }

  @Test
  @DisplayName("ToString should contain field values")
  void toStringShouldWork() {
    StkCallback.CallbackItem item =
        StkCallback.CallbackItem.builder().name("Amount").value(100.0).build();
    assertTrue(item.toString().contains("Amount"));
  }
}

package io.github.openpaydev.mpesa.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.openpaydev.mpesa.core.models.StkCallback;

/**
 * A utility class for parsing the JSON callback sent by the M-Pesa API.
 *
 * <p>The M-Pesa API communicates the result of an STK Push transaction by sending an HTTP POST
 * request to a callback URL that you provide. The body of this request contains a JSON object with
 * the transaction details. This class provides a simple, static method to parse that JSON.
 *
 * <p>Example Usage in a Spring Boot Controller:
 *
 * <pre>{@code
 * import org.springframework.web.bind.annotation.PostMapping;
 * import org.springframework.web.bind.annotation.RequestBody;
 * import org.springframework.web.bind.annotation.RestController;
 *
 * {@literal @}RestController
 * public class MpesaCallbackController {
 *
 *     {@literal @}PostMapping("/mpesa-callback")
 *     public void handleMpesaCallback({@literal @}RequestBody String callbackJson) {
 *         try {
 *             StkCallback stkCallback = MpesaCallbackParser.parse(callbackJson);
 *             // Now you can inspect the callback object
 *             int resultCode = stkCallback.getBody().getStkCallback().getResultCode();
 *             if (resultCode == 0) {
 *                 System.out.println("Payment successful!");
 *                 // TODO: Update your database, notify the user, etc.
 *             } else {
 *                 System.out.println("Payment failed or was cancelled. Reason: " + stkCallback.getBody().getStkCallback().getResultDesc());
 *             }
 *         } catch (JsonProcessingException e) {
 *             System.err.println("Error parsing M-Pesa callback: " + e.getMessage());
 *             // Handle the error appropriately
 *         }
 *     }
 * }
 * }</pre>
 */
public final class MpesaCallbackParser {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  /** Private constructor to prevent instantiation of this utility class. */
  private MpesaCallbackParser() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * Parses the JSON string from an M-Pesa callback into a structured {@link StkCallback} object.
   *
   * @param jsonCallbackData The raw JSON string received from the M-Pesa API in the callback
   *     request body.
   * @return A deserialized {@link StkCallback} object containing the transaction results.
   * @throws JsonProcessingException if the provided JSON string is malformed or cannot be parsed
   *     into the target object.
   */
  public static StkCallback parse(String jsonCallbackData) throws JsonProcessingException {
    return objectMapper.readValue(jsonCallbackData, StkCallback.class);
  }
}

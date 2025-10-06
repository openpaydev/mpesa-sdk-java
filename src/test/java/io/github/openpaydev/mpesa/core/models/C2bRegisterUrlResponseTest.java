package io.github.openpaydev.mpesa.core.models;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class C2bRegisterUrlResponseTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("Should correctly deserialize a C2B URL Registration JSON response")
  void shouldDeserializeFromJsonCorrectly() throws Exception {
    String c2bResponseJson =
        "{\n"
            + "    \"OriginatorConverstionID\": \"AG_20251022_0000112233ABC\",\n"
            + "    \"ConversationID\": \"AG_20251022_0000112233DEF\",\n"
            + "    \"ResponseDescription\": \"success\"\n"
            + "}";

    C2bRegisterUrlResponse response =
        objectMapper.readValue(c2bResponseJson, C2bRegisterUrlResponse.class);

    assertNotNull(response);
    assertEquals("AG_20251022_0000112233ABC", response.getOriginatorConversationID());
    assertEquals("AG_20251022_0000112233DEF", response.getConversationID());
    assertEquals("success", response.getResponseDescription());
  }
}

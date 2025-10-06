package io.github.openpaydev.mpesa.core.models;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccessTokenResponseTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("Should correctly deserialize a valid JSON response")
  void shouldDeserializeFromJson() throws JsonProcessingException {
    String jsonResponse =
        new StringBuilder()
            .append("{\n")
            .append("    \"access_token\": \"aBcDeFgHiJkLmNoPqRsT\",\n")
            .append("    \"expires_in\": \"3599\"\n")
            .append("}")
            .toString();

    AccessTokenResponse accessTokenResponse =
        objectMapper.readValue(jsonResponse, AccessTokenResponse.class);

    assertNotNull(accessTokenResponse);
    assertEquals("aBcDeFgHiJkLmNoPqRsT", accessTokenResponse.getAccessToken());
    assertEquals(3599L, accessTokenResponse.getExpiresIn());
  }

  @Test
  @DisplayName("Should handle JSON with extra fields gracefully")
  void shouldDeserializeWithExtraFields() throws JsonProcessingException {
    String jsonWithExtraField =
        new StringBuilder()
            .append("{\n")
            .append("    \"access_token\": \"aBcDeFgHiJkLmNoPqRsT\",\n")
            .append("    \"expires_in\": \"3599\",\n")
            .append("    \"scope\": \"default\"\n")
            .append("}")
            .toString();

    AccessTokenResponse accessTokenResponse =
        objectMapper.readValue(jsonWithExtraField, AccessTokenResponse.class);

    assertNotNull(accessTokenResponse);
    assertEquals("aBcDeFgHiJkLmNoPqRsT", accessTokenResponse.getAccessToken());
    assertEquals(3599L, accessTokenResponse.getExpiresIn());
  }

  @Test
  @DisplayName("ToString should contain the field values")
  void toStringShouldContainFieldValues() {
    AccessTokenResponse accessTokenResponse =
        AccessTokenResponse.builder().accessToken("myTestToken123").expiresIn(3599L).build();

    String stringResponse = accessTokenResponse.toString();

    assertTrue(
        stringResponse.contains("accessToken=myTestToken123"),
        "ToString should include the access token.");
    assertTrue(
        stringResponse.contains("expiresIn=3599"), "ToString should include the expires in value.");
  }
}

package io.github.openpaydev.mpesa.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents the response object received from the M-Pesa OAuth token generation endpoint.
 *
 * <p>This is an internal model used by the {@link
 * io.github.openpaydev.mpesa.core.auth.TokenManager} to deserialize the authentication response.
 */
@Value
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenResponse {

  /** The actual OAuth 2.0 access token used for authorizing API requests. */
  @JsonProperty("access_token")
  String accessToken;

  /**
   * The duration for which the access token is valid, in seconds. The M-Pesa API typically returns
   * "3599" for a one-hour validity.
   */
  @JsonProperty("expires_in")
  long expiresIn;
}

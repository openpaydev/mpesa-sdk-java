package io.github.openpaydev.mpesa.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.openpaydev.mpesa.backend.auth.MpesaTokenManager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents the response object received from the M-Pesa OAuth token generation endpoint.
 * <p>
 * This is an internal model used by the {@link MpesaTokenManager}
 * to deserialize the authentication response.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AccessTokenResponse {

    /**
     * The actual OAuth 2.0 access token used for authorizing API requests.
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * The duration for which the access token is valid, in seconds.
     * The M-Pesa API typically returns "3599" for a one-hour validity.
     */
    @JsonProperty("expires_in")
    private long expiresIn;
}
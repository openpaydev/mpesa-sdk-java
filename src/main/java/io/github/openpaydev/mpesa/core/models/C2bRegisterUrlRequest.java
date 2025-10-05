package io.github.openpaydev.mpesa.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents the request payload for registering the C2B confirmation and validation URLs.
 */
@Value
@Jacksonized
@Builder(toBuilder = true)
public class C2bRegisterUrlRequest {

    /**
     * The short code of the organization receiving the payment.
     */
    @JsonProperty("ShortCode")
    String shortCode;

    /**
     * The default action to be taken if the validation URL is unreachable.
     * M-Pesa will either complete or cancel the transaction.
     */
    @JsonProperty("ResponseType")
    C2bResponseType responseType;

    /**
     * The URL that receives the confirmation of a successful transaction.
     * This URL is hit after the transaction is completed.
     */
    @JsonProperty("ConfirmationURL")
    String confirmationUrl;

    /**
     * The URL that M-Pesa hits to validate the transaction before processing it.
     * Your system should respond to this callback to either accept or reject the payment.
     */
    @JsonProperty("ValidationURL")
    String validationUrl;
}
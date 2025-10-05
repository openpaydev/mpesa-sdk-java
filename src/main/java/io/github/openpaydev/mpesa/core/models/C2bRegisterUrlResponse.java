package io.github.openpaydev.mpesa.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents the response received from M-Pesa after a C2B URL registration request.
 */
@Value
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class C2bRegisterUrlResponse {

    /**
     * A unique identifier for the transaction request originator.
     */
    @JsonProperty("OriginatorConverstionID")
    String originatorConversationID;

    /**
     * A unique identifier for the conversation.
     */
    @JsonProperty("ConversationID")
    String conversationID;

    /**
     * A description of the response, e.g., "success".
     */
    @JsonProperty("ResponseDescription")
    String responseDescription;
}
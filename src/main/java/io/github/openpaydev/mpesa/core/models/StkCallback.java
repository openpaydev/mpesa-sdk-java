package io.github.openpaydev.mpesa.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents the entire JSON object received from M-Pesa on the callback URL after a transaction.
 * <p>
 * This is the root object of the callback payload.
 */
@Value
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class StkCallback {

    @JsonProperty("Body")
    Body body;

    /**
     * The main container for the callback data.
     */
    @Value
    @Jacksonized
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @JsonProperty("stkCallback")
        StkCallbackData stkCallback;
    }

    /**
     * Contains the core details and result of the STK Push transaction.
     */
    @Value
    @Jacksonized
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StkCallbackData {
        @JsonProperty("MerchantRequestID")
        String merchantRequestID;

        @JsonProperty("CheckoutRequestID")
        String checkoutRequestID;

        @JsonProperty("ResultCode")
        int resultCode;

        @JsonProperty("ResultDesc")
        String resultDesc;

        @JsonProperty("CallbackMetadata")
        CallbackMetadata callbackMetadata;
    }

    /**
     * A container for the list of metadata items.
     */
    @Value
    @Jacksonized
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CallbackMetadata {
        @JsonProperty("Item")
        CallbackItem[] items;
    }

    /**
     * A key-value pair representing a piece of transaction metadata.
     */
    @Value
    @Jacksonized
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CallbackItem {
        @JsonProperty("Name")
        String name;

        @JsonProperty("Value")
        Object value;
    }
}
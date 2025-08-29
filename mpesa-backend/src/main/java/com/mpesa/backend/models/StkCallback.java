package com.mpesa.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StkCallback {

    @JsonProperty("Body")
    private Body body;

    @Getter
    @Setter
    public static class Body {

        @JsonProperty("stkCallback")
        private StkCallbackData stkCallback;

    }

    @Getter
    @Setter
    public static class StkCallbackData {

        @JsonProperty("MerchantRequestID")
        private String merchantRequestID;

        @JsonProperty("CheckoutRequestID")
        private String checkoutRequestID;

        @JsonProperty("ResultCode")
        private int resultCode;

        @JsonProperty("ResultDesc")
        private String resultDesc;

        @JsonProperty("CallbackMetadata")
        private CallbackMetadata callbackMetadata;

    }

    @Getter
    @Setter
    public static class CallbackMetadata {

        @JsonProperty("Item")
        private CallbackItem[] items;

    }

    @Getter
    @Setter
    public static class CallbackItem {

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Value")
        private Object value;
    }
}

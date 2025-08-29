package com.mpesa.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response model for STK Push transaction result.
 */
public class StkPushResponse {

    @JsonProperty("MerchantRequestID")
    private String merchantRequestID;
    @JsonProperty("CheckoutRequestID")
    private String checkoutRequestID;
    @JsonProperty("ResponseCode")
    private String responseCode;
    @JsonProperty("ResponseDescription")
    private String responseDescription;
    @JsonProperty("CustomerMessage")
    private String customerMessage;

    /** Default constructor */
    public StkPushResponse() {}

    /**
     * @return Merchant request ID assigned by Mpesa
     */
    public String getMerchantRequestID() {
        return merchantRequestID;
    }

    /**
     * @param merchantRequestID Merchant request ID assigned by Mpesa
     */
    public void setMerchantRequestID(String merchantRequestID) {
        this.merchantRequestID = merchantRequestID;
    }

    /**
     * @return Checkout request ID assigned by Mpesa
     */
    public String getCheckoutRequestID() {
        return checkoutRequestID;
    }

    /**
     * @param checkoutRequestID Checkout request ID assigned by Mpesa
     */
    public void setCheckoutRequestID(String checkoutRequestID) {
        this.checkoutRequestID = checkoutRequestID;
    }

    /**
     * @return Response code from Mpesa API
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * @param responseCode Response code from Mpesa API
     */
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * @return Description of the response
     */
    public String getResponseDescription() {
        return responseDescription;
    }

    /**
     * @param responseDescription Description of the response
     */
    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    /**
     * @return Message intended for the customer
     */
    public String getCustomerMessage() {
        return customerMessage;
    }

    /**
     * @param customerMessage Message intended for the customer
     */
    public void setCustomerMessage(String customerMessage) {
        this.customerMessage = customerMessage;
    }

    @Override
    public String toString() {
        return "STK Push Response:\n" +
                "MerchantRequestID = " + merchantRequestID + "\n" +
                "CheckoutRequestID = " + checkoutRequestID + "\n" +
                "ResponseCode = " + responseCode + "\n" +
                "ResponseDescription = " + responseDescription + "\n" +
                "CustomerMessage = " + customerMessage;
    }
}

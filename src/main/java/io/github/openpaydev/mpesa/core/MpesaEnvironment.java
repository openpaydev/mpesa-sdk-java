package io.github.openpaydev.mpesa.core;

/**
 * Enum representing the M-Pesa API environments and their corresponding endpoint URLs.
 * <p>
 * This enum centralizes all API endpoint URLs, ensuring that the client code
 * does not need to be aware of the specific paths for authentication or transactions.
 */
public enum MpesaEnvironment {

    /**
     * The Sandbox environment for development and testing purposes.
     */
    SANDBOX("https://sandbox.safaricom.co.ke"),

    /**
     * The Production environment for live, real-money transactions.
     */
    PRODUCTION("https://api.safaricom.co.ke");

    private final String baseUrl;

    MpesaEnvironment(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Returns the full URL for the OAuth token generation endpoint.
     *
     * @return The complete authentication URL as a String.
     */
    public String getAuthUrl() {
        return baseUrl + "/oauth/v1/generate?grant_type=client_credentials";
    }

    /**
     * Returns the full URL for the STK Push (Lipa Na M-Pesa Online) process request endpoint.
     *
     * @return The complete STK Push URL as a String.
     */
    public String getStkPushUrl() {
        return baseUrl + "/mpesa/stkpush/v1/processrequest";
    }

    /**
     * Returns the full URL for the STK Push Query (Transaction Status) endpoint.
     *
     * @return The complete STK Push Query URL as a String.
     */
    public String getStkQueryUrl() {
        return baseUrl + "/mpesa/stkpushquery/v1/query";
    }

    /**
     * Returns the full URL for the C2B Register URL endpoint.
     *
     * @return The complete C2B Register URL as a String.
     */
    public String getC2bRegisterUrl() {
        return baseUrl + "/mpesa/c2b/v1/registerurl";
    }
}
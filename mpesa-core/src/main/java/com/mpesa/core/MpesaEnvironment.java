package com.mpesa.core;

/**
 * Enum representing the Mpesa API environments.
 */
public enum MpesaEnvironment {

    /**
     * Sandbox environment URL for testing.
     */
    SANDBOX("https://sandbox.safaricom.co.ke"),

    /**
     * Production environment URL for live transactions.
     */
    PRODUCTION("https://api.safaricom.co.ke");

    private final String baseUrl;

    /**
     * Constructor to set the base URL for the environment.
     *
     * @param baseUrl The base URL of the Mpesa environment.
     */
    MpesaEnvironment(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Returns the base URL of the environment.
     *
     * @return The base URL as a String.
     */
    public String getBaseUrl() {
        return baseUrl;
    }
}

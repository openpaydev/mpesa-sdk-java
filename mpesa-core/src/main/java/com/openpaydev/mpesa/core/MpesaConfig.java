package com.openpaydev.mpesa.core;

import lombok.Builder;

/**
 * Configuration for Mpesa API client, including credentials and environment.
 */
@Builder
public class MpesaConfig {

    private final String consumerKey;
    private final String consumerSecret;
    private final String shortCode;
    private final String passKey;
    private final MpesaEnvironment environment;

    /**
     * Constructs a new MpesaConfig instance.
     *
     * @param consumerKey The API consumer key.
     * @param consumerSecret The API consumer secret.
     * @param shortCode The business shortcode.
     * @param passKey The passkey used for STK push requests.
     * @param environment The Mpesa environment (SANDBOX or PRODUCTION).
     */
    public MpesaConfig(String consumerKey, String consumerSecret, String shortCode,
                       String passKey, MpesaEnvironment environment) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.shortCode = shortCode;
        this.passKey = passKey;
        this.environment = environment;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getPassKey() {
        return passKey;
    }

    public MpesaEnvironment getEnvironment() {
        return environment;
    }

    /**
     * Convenience method for creating config from environment variables.
     */
    public static MpesaConfig fromEnv() {
        return MpesaConfig.builder()
                .consumerKey(System.getenv("MPESA_CONSUMER_KEY"))
                .consumerSecret(System.getenv("MPESA_CONSUMER_SECRET"))
                .shortCode(System.getenv("MPESA_SHORTCODE"))
                .passKey(System.getenv("MPESA_PASSKEY"))
                .environment(
                        "PRODUCTION".equalsIgnoreCase(System.getenv("MPESA_ENVIRONMENT"))
                                ? MpesaEnvironment.PRODUCTION
                                : MpesaEnvironment.SANDBOX
                )
                .build();
    }
}

package io.github.openpaydev.mpesa.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MpesaEnvironmentTest {

    @Test
    @DisplayName("Should return correct URLs for SANDBOX environment")
    void sandboxUrlsAreCorrect() {
        // Given
        MpesaEnvironment env = MpesaEnvironment.SANDBOX;
        String expectedBaseUrl = "https://sandbox.safaricom.co.ke";

        // When & Then
        assertEquals(expectedBaseUrl + "/oauth/v1/generate?grant_type=client_credentials", env.getAuthUrl());
        assertEquals(expectedBaseUrl + "/mpesa/stkpush/v1/processrequest", env.getStkPushUrl());
        assertEquals(expectedBaseUrl + "/mpesa/stkpushquery/v1/query", env.getStkQueryUrl());
    }

    @Test
    @DisplayName("Should return correct URLs for PRODUCTION environment")
    void productionUrlsAreCorrect() {
        // Given
        MpesaEnvironment env = MpesaEnvironment.PRODUCTION;
        String expectedBaseUrl = "https://api.safaricom.co.ke";

        // When & Then
        assertEquals(expectedBaseUrl + "/oauth/v1/generate?grant_type=client_credentials", env.getAuthUrl());
        assertEquals(expectedBaseUrl + "/mpesa/stkpush/v1/processrequest", env.getStkPushUrl());
        assertEquals(expectedBaseUrl + "/mpesa/stkpushquery/v1/query", env.getStkQueryUrl());
    }
}
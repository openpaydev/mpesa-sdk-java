package io.github.openpaydev.mpesa.core.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class C2bValidationResultTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("success() factory should create a success result with ResultCode 0")
    void successFactory_shouldCreateSuccessResult() throws Exception {
        C2bValidationResult successResult = C2bValidationResult.success("Accepted");

        assertEquals(0, successResult.getResultCode());
        assertEquals("Accepted", successResult.getResultDesc());

        String json = objectMapper.writeValueAsString(successResult);
        assertTrue(json.contains("\"ResultCode\":0"));
        assertTrue(json.contains("\"ResultDesc\":\"Accepted\""));
    }

    @Test
    @DisplayName("error() factory should create an error result with ResultCode 1")
    void errorFactory_shouldCreateErrorResult() throws Exception {
        C2bValidationResult errorResult = C2bValidationResult.error("Rejected");

        assertEquals(1, errorResult.getResultCode());
        assertEquals("Rejected", errorResult.getResultDesc());

        String json = objectMapper.writeValueAsString(errorResult);
        assertTrue(json.contains("\"ResultCode\":1"));
        assertTrue(json.contains("\"ResultDesc\":\"Rejected\""));
    }
}
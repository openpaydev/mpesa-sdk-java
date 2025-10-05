package io.github.openpaydev.mpesa.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the JSON response your application must send back to M-Pesa
 * from your validation URL.
 */
@Getter
@AllArgsConstructor
public class C2bValidationResult {
    @JsonProperty("ResultCode")
    private int resultCode;

    @JsonProperty("ResultDesc")
    private String resultDesc;

    /**
     * Helper to create a standard success response.
     */
    public static C2bValidationResult success(String message) {
        return new C2bValidationResult(0, message);
    }

    /**
     * Helper to create a standard error response.
     */
    public static C2bValidationResult error(String message) {
        return new C2bValidationResult(1, message);
    }
}
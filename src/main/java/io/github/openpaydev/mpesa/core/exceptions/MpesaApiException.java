package io.github.openpaydev.mpesa.core.exceptions;

/**
 * Thrown when the M-Pesa API returns a non-successful HTTP response (e.g., 4xx or 5xx).
 * This exception provides access to the HTTP status code and the raw response body,
 * which is crucial for debugging API-level errors like malformed requests.
 */
public class MpesaApiException extends MpesaException {
    private final int statusCode;
    private final String responseBody;

    public MpesaApiException(String message, int statusCode, String responseBody) {
        super(String.format("%s (Status: %d, Body: %s)", message, statusCode, responseBody));
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
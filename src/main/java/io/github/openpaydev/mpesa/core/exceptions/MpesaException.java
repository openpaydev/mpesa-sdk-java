package io.github.openpaydev.mpesa.core.exceptions;

public class MpesaException extends Exception {
    public MpesaException(String message) {
        super(message);
    }

    public MpesaException(String message, Throwable cause) {
        super(message, cause);
    }
}
package io.github.openpaydev.mpesa.core.models;

/**
 * Enum representing the response type for C2B URL registration.
 * This determines the default behavior if the validation URL is unreachable.
 */
public enum C2bResponseType {
    /**
     * If the validation URL is unreachable, the transaction will be automatically cancelled.
     */
    Cancelled,

    /**
     * If the validation URL is unreachable, the transaction will be automatically completed.
     */
    Completed
}
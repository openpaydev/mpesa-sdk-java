package io.github.openpaydev.mpesa.core.service;

import io.github.openpaydev.mpesa.core.exceptions.MpesaException;
import io.github.openpaydev.mpesa.core.models.StkPushRequest;
import io.github.openpaydev.mpesa.core.models.StkPushResponse;
import io.github.openpaydev.mpesa.core.models.StkStatusQueryResponse;

public interface StkPushService {
    /**
     * Initiates an M-Pesa STK Push request.
     * @param request The STK Push request object.
     * @return The initial synchronous response from the API.
     * @throws MpesaException If a network or API error occurs.
     */
    StkPushResponse stkPush(StkPushRequest request) throws MpesaException;

    /**
     * Queries the status of an STK Push transaction.
     * @param checkoutRequestID The unique ID of the transaction to query.
     * @return The response containing the transaction status details.
     * @throws MpesaException If a network or API error occurs.
     */
    StkStatusQueryResponse queryStkStatus(String checkoutRequestID) throws MpesaException;
}

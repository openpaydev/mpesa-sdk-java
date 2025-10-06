package io.github.openpaydev.mpesa.core.service;

import io.github.openpaydev.mpesa.core.exceptions.MpesaException;
import io.github.openpaydev.mpesa.core.models.C2bRegisterUrlRequest;
import io.github.openpaydev.mpesa.core.models.C2bRegisterUrlResponse;

public interface C2bService {

  /**
   * Registers the Confirmation and Validation URLs for a C2B shortcode.
   *
   * @param request The C2B registration request object.
   * @return The response confirming the registration.
   * @throws MpesaException If a network or API error occurs.
   */
  C2bRegisterUrlResponse registerC2bUrl(C2bRegisterUrlRequest request) throws MpesaException;
}

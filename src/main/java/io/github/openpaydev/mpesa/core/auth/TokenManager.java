package io.github.openpaydev.mpesa.core.auth;

import io.github.openpaydev.mpesa.core.exceptions.MpesaAuthException;

public interface TokenManager {
  /**
   * Returns a valid access token, fetching a new one if necessary.
   *
   * @return A valid OAuth access token string.
   * @throws MpesaAuthException If an error occurs during the fetch.
   */
  String getAccessToken() throws MpesaAuthException;
}

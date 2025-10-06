package io.github.openpaydev.mpesa.core.exceptions;

public class MpesaAuthException extends MpesaException {
  private static final long serialVersionUID = 2L;

  public MpesaAuthException(String message, Throwable cause) {
    super(message, cause);
  }
}

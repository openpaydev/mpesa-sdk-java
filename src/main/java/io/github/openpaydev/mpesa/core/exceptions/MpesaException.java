package io.github.openpaydev.mpesa.core.exceptions;

public class MpesaException extends Exception {
  private static final long serialVersionUID = 3L;

  public MpesaException(String message) {
    super(message);
  }

  public MpesaException(String message, Throwable cause) {
    super(message, cause);
  }
}

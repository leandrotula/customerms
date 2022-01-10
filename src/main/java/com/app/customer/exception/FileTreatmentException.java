package com.app.customer.exception;

public class FileTreatmentException extends RuntimeException {

  public FileTreatmentException() {
  }

  public FileTreatmentException(String message) {
    super(message);
  }

  public FileTreatmentException(String message, Throwable cause) {
    super(message, cause);
  }
}

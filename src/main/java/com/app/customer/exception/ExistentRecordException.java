package com.app.customer.exception;

public class ExistentRecordException extends RuntimeException {

  public ExistentRecordException() {
  }

  public ExistentRecordException(String message) {
    super(message);
  }

  public ExistentRecordException(String message, Throwable cause) {
    super(message, cause);
  }
}

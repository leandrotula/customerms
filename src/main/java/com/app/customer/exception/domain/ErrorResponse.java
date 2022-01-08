package com.app.customer.exception.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ErrorResponse {

  private final String message;
  private final List<String> details;
}

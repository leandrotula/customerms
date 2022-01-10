package com.app.customer.service.customer.impl;

import com.app.customer.service.customer.CustomerFlow;
import com.app.customer.service.customer.flow.AuthenticatedUserConverter;
import com.app.customer.service.customer.flow.FileFlowFunction;
import com.app.customer.service.domain.Customer;
import com.app.customer.service.domain.CustomerFlowResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CustomerFlowConverter implements CustomerFlow {

  private final AuthenticatedUserConverter authenticatedUserConverter;
  private final FileFlowFunction fileFlowFunction;

  public CustomerFlowResult process(String loggerUsername, MultipartFile multipartFile, String customer) {

    return CustomerFlowResult
        .builder()
        .user(authenticatedUserConverter.apply(loggerUsername))
        .photoUrl(fileFlowFunction.apply(multipartFile))
        .customer(convertStrintToCutomerObject(customer))
        .build();
  }

  private Customer convertStrintToCutomerObject(final String customer) {
    final ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readValue(customer, Customer.class);
    } catch (Exception e) {
      throw new RuntimeException("Invalid state, could not convert to customer ", e);
    }
  }

}

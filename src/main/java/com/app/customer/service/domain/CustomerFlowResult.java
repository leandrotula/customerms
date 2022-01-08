package com.app.customer.service.domain;

import com.app.customer.domain.UserEntity;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerFlowResult {

  private Customer customer;
  private UserEntity user;
  private String photoUrl;

}

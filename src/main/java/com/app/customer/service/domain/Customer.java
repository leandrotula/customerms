package com.app.customer.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

  private Long customerId;
  private String name;
  private String surname;
  private String photoUrl;
  private String loggedUserName;
}

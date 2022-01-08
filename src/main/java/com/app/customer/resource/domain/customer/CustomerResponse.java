package com.app.customer.resource.domain.customer;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = CustomerResponse.CustomerResponseBuilder.class)
@Value
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomerResponse {

  private String name;
  private String surname;
  private String photoUrl;
  private String owner;
}

package com.app.customer.resource.domain.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserResponse {

  private String name;
  private String username;
}

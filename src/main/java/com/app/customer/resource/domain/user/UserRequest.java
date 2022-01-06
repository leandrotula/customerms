package com.app.customer.resource.domain.user;

import com.app.customer.domain.Status;
import lombok.Data;

@Data
public class UserRequest {

  private String name;
  private String username;
  private Status status;
}

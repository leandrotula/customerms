package com.app.customer.service.domain;

import com.app.customer.security.Roles;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class User {

  private Long id;
  private String name;
  private String username;
  private String password;
  private String status;
  private List<Roles> roles;
}

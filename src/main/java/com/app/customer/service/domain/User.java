package com.app.customer.service.domain;

import com.app.customer.domain.RoleEntity;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class User {

  private Long id;
  private String name;
  private String username;
  private String password;
  private Collection<RoleEntity> roles;
}

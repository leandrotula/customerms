package com.app.customer.resource.domain.user;

import com.app.customer.domain.Status;
import com.app.customer.security.Roles;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserRequest {

  @NotBlank(message = "Name is mandatory")
  private String name;

  @NotBlank(message = "Username is mandatory")
  private String username;

  private Status status;

  @NotBlank(message = "A password must be provided")
  private String password;

  @NotNull(message = "For a new user, roles must be specified")
  private List<Roles> roles;
}

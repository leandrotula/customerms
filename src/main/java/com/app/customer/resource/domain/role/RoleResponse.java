package com.app.customer.resource.domain.role;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleResponse {
  private Long id;
  private String name;
}

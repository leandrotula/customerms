package com.app.customer.service.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Role {

  private Long id;
  private String name;
}

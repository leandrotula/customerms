package com.app.customer.resource.domain.role;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = RoleResponse.RoleResponseBuilder.class)
@Value
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RoleResponse {
  private Long id;
  private String name;
}

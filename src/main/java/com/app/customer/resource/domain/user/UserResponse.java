package com.app.customer.resource.domain.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@JsonDeserialize(builder = UserResponse.UserResponseBuilder.class)
@Value
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponse {

  private String name;
  private Long id;
  private String username;
  private String status;
  private List<String> roles;
}

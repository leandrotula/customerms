package com.app.customer.resource.controllers;

import com.app.customer.resource.domain.role.RoleRequest;
import com.app.customer.resource.domain.role.RoleResponse;
import com.app.customer.service.domain.Role;
import com.app.customer.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController("/v1/api")
@RequiredArgsConstructor
public class RoleController {

  private final RoleService roleService;

  @PostMapping("/role")
  public ResponseEntity<RoleResponse> saveRole(@RequestBody RoleRequest roleRequest) {

    Role role = createRole(roleRequest);
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/api/role").toUriString());
    Role savedRole = roleService.save(role);
    return ResponseEntity.created(uri).body(createRoleResponse(savedRole));
  }

  private RoleResponse createRoleResponse(Role role) {

    final RoleResponse roleResponse = new RoleResponse();
    roleResponse.setId(role.getId());
    roleResponse.setName(role.getName());

    return roleResponse;
  }

  private Role createRole(RoleRequest roleBody) {

    return Role.builder()
        .id(roleBody.getId())
        .name(roleBody.getName())
        .build();
  }
}

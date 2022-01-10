package com.app.customer.service.role.impl;

import com.app.customer.domain.RoleEntity;
import com.app.customer.repository.RoleRepository;
import com.app.customer.service.domain.Role;
import com.app.customer.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepository;

  @Override
  public Role save(Role role) {
    RoleEntity save = roleRepository.save(createRoleEntity(role));
    return createRole(save);
  }

  private RoleEntity createRoleEntity(Role role) {
    final RoleEntity roleEntity = new RoleEntity();
    roleEntity.setName(role.getName());
    return roleEntity;
  }

  private Role createRole(RoleEntity roleEntity) {
    return Role.builder().name(roleEntity.getName()).build();
  }
}

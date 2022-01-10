package com.app.customer.service;

import com.app.customer.domain.RoleEntity;
import com.app.customer.domain.UserEntity;
import com.app.customer.security.Roles;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;

@UtilityClass
public class Commons {

  public static UserEntity createUserEntity() {
    UserEntity user = new UserEntity();
    user.setUsername("usersurname");
    user.setName("username");
    user.setId(1L);
    RoleEntity roleEntity = new RoleEntity();
    roleEntity.setName(Roles.ADMIN_ROLE.name());
    Collection<RoleEntity> roles = List.of(roleEntity);
    user.setRoles(roles);
    return user;
  }
}

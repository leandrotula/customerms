package com.app.customer.service.user.impl;

import com.app.customer.domain.UserEntity;
import com.app.customer.repository.RoleRepository;
import com.app.customer.repository.UserRepository;
import com.app.customer.service.domain.User;
import com.app.customer.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public User saveUser(User user) {
    log.info("saving user to database");
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    return convertToUser(userRepository.save(mapUserToUserEntity(user)));
  }

  @Override
  public void assignRoleToUser(String username, String roleName) {

    log.info("Adding role {} to username {} ", roleName, username);
    Optional<UserEntity> byUsername = userRepository.findByUsername(username);

    byUsername.ifPresent(user -> roleRepository.findByName(roleName).ifPresent(roleFound -> user.getRoles().add(roleFound)));

  }

  @Override
  public Optional<User> getUser(String username) {

    log.info("fetching user {} ", username);
    Optional<UserEntity> userEntity = userRepository.findByUsername(username);
    return userEntity.map(this::convertToUser);
  }

  @Override
  public List<User> getAllUsers(Integer pageNumber, Integer pageSize, String sortBy) {
    log.info("GetAll users ");
    Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));

    Page<UserEntity> pagedResult = userRepository.findAll(paging);

    if(pagedResult.hasContent()) {
      List<UserEntity> userEntities = pagedResult.getContent();
      return mapToUserDomain(userEntities);
    } else {
      return Collections.emptyList();
    }
  }

  private List<User> mapToUserDomain(List<UserEntity> userEntities) {
    return userEntities.stream().map(this::convertToUser).collect(Collectors.toList());
  }

  private User convertToUser(UserEntity userEntity) {
    return User.builder()
        .id(userEntity.getId())
        .name(userEntity.getName())
        .username(userEntity.getUsername())
        .password(userEntity.getPassword())
        .roles(userEntity.getRoles())
        .build();
  }

  private UserEntity mapUserToUserEntity(final User user) {

    final UserEntity userEntity = new UserEntity();
    userEntity.setUsername(user.getUsername());
    userEntity.setPassword(user.getPassword());
    userEntity.setRoles(user.getRoles());
    userEntity.setName(user.getName());

    return userEntity;
  }
}

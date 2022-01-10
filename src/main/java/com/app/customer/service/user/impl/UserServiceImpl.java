package com.app.customer.service.user.impl;

import com.app.customer.domain.RoleEntity;
import com.app.customer.domain.Status;
import com.app.customer.domain.UserEntity;
import com.app.customer.exception.ExistentRecordException;
import com.app.customer.exception.NotFoundException;
import com.app.customer.exception.ServiceException;
import com.app.customer.repository.RoleRepository;
import com.app.customer.repository.UserRepository;
import com.app.customer.resource.domain.user.StatusRequest;
import com.app.customer.resource.domain.user.UserRequest;
import com.app.customer.security.Roles;
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
  public User save(UserRequest userRequest) {

    User user = User.builder()
        .name(userRequest.getName())
        .username(userRequest.getUsername())
        .password(userRequest.getPassword())
        .roles(userRequest.getRoles())
        .build();

    Optional<UserEntity> optUserEntity = userRepository.findByUsernameAndStatusIgnoreCase(user.getUsername().toLowerCase(), Status.ACTIVE.name());
    if(optUserEntity.isPresent()) {
      throw new ExistentRecordException(String.format("User was already present with username %s ", user.getUsername()));
    }
    log.info("saving user to database");
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    return convertToUser(userRepository.save(mapUserToUserEntity(user)));
  }

  @Override
  public void assignRoleToUser(String username, String roleName) {

    log.info("Adding role {} to username {} ", roleName, username);
    Optional<UserEntity> byUsername = userRepository.findByUsernameAndStatusIgnoreCase(username, Status.ACTIVE.name());

    byUsername.ifPresent(user -> roleRepository.findByName(roleName).ifPresent(roleFound -> user.getRoles().add(roleFound)));

  }

  @Override
  public Optional<User> getUserByUsernameAndStatusActive(String username) {

    log.info("fetching user {} ", username);
    Optional<UserEntity> userEntity = userRepository.findByUsernameAndStatusIgnoreCase(username.toLowerCase(), Status.ACTIVE.name());
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

  @Override
  public void delete(Long userId) {

    Optional<UserEntity> optionalUser = userRepository.findByIdAndStatus(userId, Status.ACTIVE.name());

    optionalUser.map(user -> {
      user.setStatus(Status.INACTIVE.name());
      return userRepository.save(user);
    }).orElseThrow(()-> new RuntimeException("User not found"));
  }

  @Override
  public User update(Long userId, UserRequest userRequest) {
    Optional<UserEntity> userFound = userRepository.findById(userId);
    if (userFound.isPresent()) {
      List<RoleEntity> roleEntities = getRoleEntities(userRequest.getRoles());

      List<RoleEntity> savedRoles = roleRepository.saveAll(roleEntities);
      UserEntity userEntity = userFound.get();
      userEntity.setStatus(userRequest.getStatus().name());
      userEntity.setUsername(userRequest.getUsername());
      userEntity.setName(userRequest.getName());
      userEntity.setRoles(savedRoles);
      userEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
      UserEntity updatedUser = userRepository.save(userEntity);

      return convertToUser(updatedUser);
    } else {
      throw new NotFoundException(String.format("User not found for id %s ", userId));
    }
  }

  @Override
  public User updateStatus(Long userId, StatusRequest statusRequest) {
    Optional<UserEntity> userFound = userRepository.findByIdAndStatus(userId, Status.ACTIVE.name());

    if (userFound.isPresent()) {
      UserEntity userEntity = userFound.get();

      boolean containsDesiredRole = userEntity.getRoles().stream().map(RoleEntity::getName)
          .collect(Collectors.toList()).contains(Roles.ADMIN_ROLE.name());
      if (!containsDesiredRole) {

        throw new ServiceException("Invalid operation, user does not have a desired role. This operation only changes admin status");
      }
      userEntity.setStatus(statusRequest.getStatus().name());

      UserEntity newUserWithUpdatedStatus = userRepository.save(userEntity);
      UserEntity savedUser = userRepository.save(newUserWithUpdatedStatus);

      return convertToUser(savedUser);
    } else {
      throw new NotFoundException("User not found");
    }
  }

  @Override
  public boolean existByUsernameAndStatus(String username) {
    return userRepository.existsByUsernameAndStatus(username, Status.ACTIVE.name());
  }

  // TODO extract mappers

  private List<User> mapToUserDomain(List<UserEntity> userEntities) {
    return userEntities.stream().map(this::convertToUser).collect(Collectors.toList());
  }
  private User convertToUser(UserEntity userEntity) {

    List<Roles> roles = userEntity.getRoles().stream().map(roleEntity -> Roles.valueOf(roleEntity.getName())).collect(Collectors.toList());

    return User.builder()
        .id(userEntity.getId())
        .name(userEntity.getName())
        .username(userEntity.getUsername())
        .password(userEntity.getPassword())
        .roles(roles)
        .status(userEntity.getStatus())
        .build();
  }

  private UserEntity mapUserToUserEntity(final User user) {

    final UserEntity userEntity = new UserEntity();
    userEntity.setUsername(user.getUsername());
    userEntity.setPassword(user.getPassword());

    List<RoleEntity> savedRoles = convertAndPersistRoles(user);

    userEntity.setRoles(savedRoles);
    userEntity.setName(user.getName());
    userEntity.setStatus(Status.ACTIVE.name());

    return userEntity;
  }

  private List<RoleEntity> convertAndPersistRoles(User user) {
    List<RoleEntity> result = new ArrayList<>();
    List<RoleEntity> roles = getRoleEntities(user.getRoles());
    roles.forEach(role -> {
      Optional<RoleEntity> optionalRoleEntity = roleRepository.findByName(role.getName());
      if (optionalRoleEntity.isPresent()) {
        result.add(optionalRoleEntity.get());
      } else {
        final RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(role.getName());
        RoleEntity newRole = roleRepository.save(roleEntity);
        result.add(newRole);
      }
    });
    return roleRepository.saveAll(result);
  }

  private List<RoleEntity> getRoleEntities(List<Roles> userRequest) {
    return userRequest.stream().map(roles -> {
      final RoleEntity roleEntity = new RoleEntity();
      roleEntity.setName(roles.name());
      return roleEntity;
    }).collect(Collectors.toList());
  }
}

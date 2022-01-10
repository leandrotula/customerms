package com.app.customer.service;

import com.app.customer.domain.RoleEntity;
import com.app.customer.domain.Status;
import com.app.customer.domain.UserEntity;
import com.app.customer.exception.ExistentRecordException;
import com.app.customer.exception.NotFoundException;
import com.app.customer.repository.RoleRepository;
import com.app.customer.repository.UserRepository;
import com.app.customer.resource.domain.user.UserRequest;
import com.app.customer.security.Roles;
import com.app.customer.service.user.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.app.customer.service.Commons.createUserEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  void saveUser_ifUserAlreadyPresent_thenThrowError() {

    UserEntity userEntity = new UserEntity();
    Optional<UserEntity> optUserEntity = Optional.of(userEntity);
    when(userRepository.findByUsernameAndStatusIgnoreCase(any(), any())).thenReturn(optUserEntity);

    final UserRequest userRequest = new UserRequest();
    userRequest.setName("test");
    userRequest.setUsername("test");
    Assertions.assertThrows(ExistentRecordException.class, () -> userService.save(userRequest));

    Mockito.verify(userRepository, never()).save(any());
    Mockito.verify(passwordEncoder, never()).encode(any());


  }

  @Test
  void saveUser_ifUserNotPresent_thenCallSave() {

    when(userRepository.findByUsernameAndStatusIgnoreCase(any(), any())).thenReturn(Optional.empty());

    when(passwordEncoder.encode(any())).thenReturn("adfasa");

    UserEntity user = createUserEntity();
    when(userRepository.save(any())).thenReturn(user);
    final UserRequest userRequest = new UserRequest();
    userRequest.setName("test");
    userRequest.setUsername("test");
    userRequest.setRoles(List.of(Roles.ADMIN_ROLE));
    userService.save(userRequest);

    Mockito.verify(userRepository, times(1)).save(any());
    Mockito.verify(passwordEncoder, times(1)).encode(any());

  }

  @Test
  void updateUser_ifUserNotPresent_thenThrowError() {

    when(userRepository.findById(any())).thenReturn(Optional.empty());

    UserRequest userRequest = createUserRequest();
    Assertions.assertThrows(NotFoundException.class, () -> userService.update(1L, userRequest));

    Mockito.verify(userRepository, never()).save(any());
    Mockito.verify(passwordEncoder, never()).encode(any());

  }

  @Test
  void updateUser_ifUserPresent_thenCallSaveMethod() {

    when(userRepository.findById(any())).thenReturn(Optional.of(createUserEntity()));
    when(passwordEncoder.encode(any())).thenReturn("adfasa");
    RoleEntity roles = new RoleEntity();
    roles.setName(Roles.ADMIN_ROLE.name());
    when(roleRepository.saveAll(any())).thenReturn(List.of(roles));

    when(userRepository.save(any())).thenReturn(createUserEntity());

    UserRequest userRequest = createUserRequest();
    userService.update(1L, userRequest);

    Mockito.verify(userRepository, times(1)).save(any());
    Mockito.verify(passwordEncoder, times(1)).encode(any());
    Mockito.verify(roleRepository, times(1)).saveAll(any());

  }

  private UserRequest createUserRequest() {
    UserRequest userRequest = new UserRequest();
    userRequest.setPassword("test");
    userRequest.setName("testname");
    userRequest.setStatus(Status.ACTIVE);
    userRequest.setRoles(List.of(Roles.ADMIN_ROLE));
    return userRequest;
  }
}

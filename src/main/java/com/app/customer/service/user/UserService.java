package com.app.customer.service.user;

import com.app.customer.resource.domain.user.StatusRequest;
import com.app.customer.resource.domain.user.UserRequest;
import com.app.customer.service.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

  User save(UserRequest userRequest);

  void assignRoleToUser(String username, String roleName);

  Optional<User> getUserByUsernameAndStatusActive(String username);

  List<User> getAllUsers(Integer pageNumber, Integer pageSize, String sortBy);

  void delete(Long userId);

  User update(Long userId, UserRequest userRequest);

  User updateStatus(Long userId, StatusRequest statusRequest);

  boolean existByUsernameAndStatus(String username);
}

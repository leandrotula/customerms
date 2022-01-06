package com.app.customer.service.user;

import com.app.customer.resource.domain.user.StatusRequest;
import com.app.customer.resource.domain.user.UserRequest;
import com.app.customer.service.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

  User saveUser(User user);

  void assignRoleToUser(String username, String roleName);

  Optional<User> getUser(String username);

  List<User> getAllUsers(Integer pageNumber, Integer pageSize, String sortBy);

  void delete(Long userId);

  User update(Long userId, String password, UserRequest userRequest);

  User updateStatus(Long userId, StatusRequest statusRequest);
}

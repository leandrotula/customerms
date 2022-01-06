package com.app.customer.resource.controllers;

import com.app.customer.resource.domain.user.StatusRequest;
import com.app.customer.resource.domain.user.UserRequest;
import com.app.customer.resource.domain.user.UserResponse;
import com.app.customer.service.domain.User;
import com.app.customer.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.app.customer.security.SecurityConstants.PASSWORD;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/users")
  public ResponseEntity<List<UserResponse>> getUsers(
      @RequestParam(defaultValue = "0") Integer pageNo,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(defaultValue = "name") String sortBy
  ) {

    List<User> users = userService.getAllUsers(pageNo, pageSize, sortBy);
    return ResponseEntity.ok(mapToUserResponse(users));
  }

  @PostMapping("/users")
  public ResponseEntity<UserResponse> save(@RequestBody UserRequest userRequest, HttpServletRequest httpServletRequest) {

    String password = httpServletRequest.getHeader("password");
    User user = User.builder().name(userRequest.getName()).username(userRequest.getUsername()).password(password).build();
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/api/users").toUriString());
    User savedUser = userService.saveUser(user);
    return ResponseEntity.created(uri).body(convertToUserResponse(savedUser));
  }

  @PutMapping("/users/{userId}")
  public ResponseEntity<UserResponse> update(@RequestBody UserRequest userRequest,
      @PathVariable Long userId, HttpServletRequest httpServletRequest) {

    String password = httpServletRequest.getHeader(PASSWORD);
    User user = userService.update(userId, password, userRequest);
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/api/users").toUriString());

    return ResponseEntity.created(uri).body(convertToUserResponse(user));
  }

  @PatchMapping("/users/{userId}")
  public ResponseEntity<UserResponse> updateStatus(@PathVariable Long userId, @RequestBody StatusRequest statusRequest) {

    User user = userService.updateStatus(userId, statusRequest);
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/api/users").toUriString());

    return ResponseEntity.created(uri).body(convertToUserResponse(user));

  }

  @DeleteMapping("/users/{userId}")
  public ResponseEntity<?> delete(@PathVariable Long userId) {

    userService.delete(userId);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/users/{username}/roles/{roleName}")
  public ResponseEntity<?> assignRoleToUsername(@PathVariable String username, @PathVariable String roleName) {

    userService.assignRoleToUser(username, roleName);
    return ResponseEntity.ok().build();
  }

  //TODO extract converters
  private List<UserResponse> mapToUserResponse(List<User> users) {
    return users.stream().map(this::convertToUserResponse).collect(Collectors.toList());
  }

  private UserResponse convertToUserResponse(User user) {
    final UserResponse userResponse = new UserResponse();
    userResponse.setUsername(user.getUsername());
    userResponse.setName(user.getName());

    return userResponse;
  }

}

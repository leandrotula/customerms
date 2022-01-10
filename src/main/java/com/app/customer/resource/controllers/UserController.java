package com.app.customer.resource.controllers;

import com.app.customer.resource.domain.user.StatusRequest;
import com.app.customer.resource.domain.user.UserRequest;
import com.app.customer.resource.domain.user.UserResponse;
import com.app.customer.security.Roles;
import com.app.customer.service.domain.User;
import com.app.customer.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
  public ResponseEntity<UserResponse> save(@RequestBody @Valid UserRequest userRequest) {

    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/api/users").toUriString());
    User savedUser = userService.save(userRequest);
    return ResponseEntity.created(uri).body(convertToUserResponse(savedUser));
  }

  @PutMapping("/users/{userId}")
  public ResponseEntity<UserResponse> update(@RequestBody @Valid UserRequest userRequest,
      @PathVariable Long userId) {

    User user = userService.update(userId, userRequest);
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/api/users").toUriString());

    return ResponseEntity.created(uri).body(convertToUserResponse(user));
  }

  @PatchMapping("/users/{userId}")
  public ResponseEntity<UserResponse> updateStatus(@PathVariable Long userId, @RequestBody @Valid StatusRequest statusRequest) {

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

    List<String> roles = user.getRoles().stream().map(Roles::name).collect(Collectors.toList());
    return UserResponse.builder()
        .name(user.getName())
        .id(user.getId())
        .status(user.getStatus())
        .roles(roles)
        .username(user.getUsername())
        .build();
  }

}

package com.app.customer.service.user.impl;

import com.app.customer.service.domain.User;
import com.app.customer.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

  private final UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    log.info("Loading user information using username {} ", username);
    Optional<User> user = userService.getUser(username);

    if (user.isEmpty()) {
      throw new RuntimeException("User not found");
    }

    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
    user.get().getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));

    return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), authorities);
  }
}

package com.app.customer.service.user.impl;

import com.app.customer.exception.NotFoundException;
import com.app.customer.service.domain.User;
import com.app.customer.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

  private final UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    log.info("Loading user information using username {} ", username);
    Optional<User> user = userService.getUserByUsernameAndStatusActive(username);

    if (user.isEmpty()) {
      throw new NotFoundException("User not found");
    }

    Set<SimpleGrantedAuthority> authorities = new HashSet<>();
    User providedUserApp = user.get();
    providedUserApp.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.name())));

    return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), authorities);
  }
}

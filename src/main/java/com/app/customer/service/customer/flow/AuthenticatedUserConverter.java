package com.app.customer.service.customer.flow;

import com.app.customer.domain.Status;
import com.app.customer.domain.UserEntity;
import com.app.customer.exception.NotFoundException;
import com.app.customer.repository.UserRepository;
import com.app.customer.service.domain.User;
import com.app.customer.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticatedUserConverter implements Function<String, UserEntity> {

  private final UserRepository userRepository;

  @Override
  public UserEntity apply(String loggedUsername) {
    Optional<UserEntity> optUserEntity = userRepository.findByUsernameAndStatusIgnoreCase(loggedUsername, Status.ACTIVE.name());
    if (optUserEntity.isEmpty()) {
      throw new NotFoundException(String.format("User not found for username {} ", loggedUsername));
    }
    return optUserEntity.get();
  }
}

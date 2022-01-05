package com.app.customer.repository;

import com.app.customer.domain.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

  Optional<UserEntity> findByUsername(final String username);
}

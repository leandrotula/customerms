package com.app.customer.repository;

import com.app.customer.domain.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

  Optional<UserEntity> findByUsernameAndStatusIgnoreCase(final String username, final String status);

  Optional<UserEntity> findByIdAndStatus(final Long userId, final String status);

  boolean existsByUsernameAndStatus(final String username, final String status);

}

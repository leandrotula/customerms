package com.app.customer.repository;

import com.app.customer.domain.CustomerEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CustomerRepository extends PagingAndSortingRepository<CustomerEntity, Long> {

  Optional<CustomerEntity> findByIdAndStatus(Long customerId, String status);

}

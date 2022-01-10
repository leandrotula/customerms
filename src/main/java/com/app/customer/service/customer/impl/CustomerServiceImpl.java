package com.app.customer.service.customer.impl;

import com.app.customer.domain.CustomerEntity;
import com.app.customer.domain.Status;
import com.app.customer.exception.ExistentRecordException;
import com.app.customer.exception.NotFoundException;
import com.app.customer.repository.CustomerRepository;
import com.app.customer.service.customer.CustomerService;
import com.app.customer.service.domain.Customer;
import com.app.customer.service.domain.CustomerFlowResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerFlowConverter customerFlowConverter;

  @Override
  public List<Customer> getAllCustomers(Integer pageNumber, Integer pageSize, String sortBy) {

    log.info("GetAll customer for page {} and size {} ", pageNumber, pageSize);
    Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));

    Page<CustomerEntity> pagedResult = customerRepository.findAll(paging);

    if(pagedResult.hasContent()) {
      List<CustomerEntity> customerEntities = pagedResult.getContent();
      return mapToCustomerDomain(customerEntities);
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public Customer getCustomerById(Long customerId) {
    return customerRepository.findById(customerId).map(this::mapToSingleCustomer)
        .orElseThrow(() -> new NotFoundException(String.format("Customer not found %s ", customerId)));
  }

  @Override
  public void delete(Long customerId) {

    Optional<CustomerEntity> byIdAndStatus = customerRepository.findByIdAndStatus(customerId, Status.ACTIVE.name());

    if(byIdAndStatus.isEmpty()) {
      throw new NotFoundException(String.format("Customer not found for id %s ", customerId));
    }

    CustomerEntity customerEntity = byIdAndStatus.get();
    customerEntity.setStatus(Status.INACTIVE.name());
    customerRepository.save(customerEntity);

  }

  @Override
  public Customer save(MultipartFile file, String customer, String loggedUser) {

    CustomerFlowResult customerFlowResult = customerFlowConverter.process(loggedUser, file, customer);
    Long id = customerFlowResult.getCustomer().getCustomerId();
    Optional<CustomerEntity> optCustomer = customerRepository.findByIdAndStatus(id, Status.ACTIVE.name());

    if(optCustomer.isPresent()) {
      throw new ExistentRecordException(String.format("Customer with id %s is already present ", id));
    }

    CustomerEntity customerEntity = convertToCustomerEntity(customerFlowResult);

    return mapToSingleCustomer(customerRepository.save(customerEntity));
  }

  @Override
  public Customer update(MultipartFile file, Long customerId, String name, String customer) {

    Optional<CustomerEntity> optCustomer = customerRepository.findByIdAndStatus(customerId, Status.ACTIVE.name());

    if (optCustomer.isEmpty()) {
      throw new NotFoundException(String.format("Customer not found for id %s ", customerId));
    }

    CustomerFlowResult customerFlowResult = customerFlowConverter.process(name, file, customer);

    CustomerEntity customerEntity = convertToCustomerEntity(customerFlowResult);

    return mapToSingleCustomer(customerRepository.save(customerEntity));
  }

  private Customer mapToSingleCustomer(CustomerEntity customerEntity) {
    return Customer.builder().customerId(customerEntity.getId())
        .name(customerEntity.getName())
        .surname(customerEntity.getSurname())
        .photoUrl(customerEntity.getPhotoUrl())
        .loggedUserName(customerEntity.getUserEntity().getUsername())
        .build();
  }

  private List<Customer> mapToCustomerDomain(List<CustomerEntity> customerEntities) {
    return customerEntities.stream().map(this::mapToSingleCustomer).collect(Collectors.toList());
  }


  private CustomerEntity convertToCustomerEntity(CustomerFlowResult customerFlowResult) {

    final CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setUserEntity(customerFlowResult.getUser());
    customerEntity.setName(customerFlowResult.getCustomer().getName());
    customerEntity.setSurname(customerFlowResult.getCustomer().getSurname());
    customerEntity.setStatus(Status.ACTIVE.name());
    customerEntity.setId(customerFlowResult.getCustomer().getCustomerId());
    customerEntity.setPhotoUrl(customerFlowResult.getPhotoUrl());

    return customerEntity;
  }
}

package com.app.customer.service.customer;

import com.app.customer.service.domain.Customer;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {

  List<Customer> getAllCustomers(Integer pageNo, Integer pageSize, String sortBy);

  Customer getCustomerById(Long customerId);

  void delete(Long customerId);

  Customer save(MultipartFile multipartFile, String customer, String loggedUser);

  Customer update(MultipartFile file, Long customerId, String name, String customer);
}

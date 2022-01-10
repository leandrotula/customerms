package com.app.customer.resource.controllers;

import com.app.customer.resource.domain.customer.CustomerResponse;
import com.app.customer.service.customer.CustomerService;
import com.app.customer.service.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;

  @PostMapping(value = "/customers")
  public ResponseEntity<CustomerResponse> save(@RequestParam(value = "file", required = false) MultipartFile file, @RequestPart("customer") String customer) throws ServletException, IOException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String name = authentication.getName();
    Customer processedCustomer = customerService.save(file, customer, name);
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/api/customers").toUriString());

    return ResponseEntity.created(uri).body(mapToSingleCustomerResponse(processedCustomer));

  }

  @GetMapping("/customers")
  public ResponseEntity<List<CustomerResponse>> getCustomers(
      @RequestParam(defaultValue = "0") Integer pageNo,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(defaultValue = "name") String sortBy
  ) {

    List<Customer> customers = customerService.getAllCustomers(pageNo, pageSize, sortBy);
    return ResponseEntity.ok(mapToCustomersResponse(customers));
  }

  @GetMapping("/customers/{customerId}")
  public ResponseEntity<CustomerResponse> getCustomers(@PathVariable Long customerId) {

    Customer customer = customerService.getCustomerById(customerId);
    return ResponseEntity.ok(mapToSingleCustomerResponse(customer));
  }

  @DeleteMapping("/customers/{customerId}")
  public ResponseEntity<?> delete(@PathVariable Long customerId) {

    customerService.delete(customerId);

    return ResponseEntity.ok().build();
  }

  @PutMapping("/customers/{customerId}")
  public ResponseEntity<CustomerResponse> update(
      @PathVariable Long customerId,
      @RequestPart("customer") String customer,
      @RequestParam(value = "file", required = false) MultipartFile file) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String name = authentication.getName();
    Customer processedCustomer = customerService.update(file, customerId, name, customer);
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/api/customers").toUriString());

    return ResponseEntity.created(uri).body(mapToSingleCustomerResponse(processedCustomer));

  }

  private List<CustomerResponse> mapToCustomersResponse(List<Customer> customers) {
    return customers.stream().map(this::mapToSingleCustomerResponse).collect(Collectors.toList());
  }

  private CustomerResponse mapToSingleCustomerResponse(Customer customer) {
    return CustomerResponse.builder()
        .name(customer.getName())
        .surname(customer.getSurname())
        .photoUrl(customer.getPhotoUrl())
        .owner(customer.getLoggedUserName())
        .build();
  }


}

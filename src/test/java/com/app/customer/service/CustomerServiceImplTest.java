package com.app.customer.service;

import com.app.customer.domain.CustomerEntity;
import com.app.customer.domain.UserEntity;
import com.app.customer.exception.NotFoundException;
import com.app.customer.repository.CustomerRepository;
import com.app.customer.service.customer.impl.CustomerFlowConverter;
import com.app.customer.service.customer.impl.CustomerServiceImpl;
import com.app.customer.service.domain.Customer;
import com.app.customer.service.domain.CustomerFlowResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.app.customer.service.Commons.createUserEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private CustomerFlowConverter customerFlowConverter;

  @InjectMocks
  private CustomerServiceImpl customerService;

  @Test
  void getCustomerById_ifUserNotFound_thenThrowException() {

    when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
    Assertions.assertThrows(NotFoundException.class, () -> customerService.getCustomerById(1L));
    Mockito.verify(customerRepository, never()).save(any());

  }

  @Test
  void getCustomerById_ifUserFound_thenCallSaveMethod() {

    Optional<CustomerEntity> customerOpt = createCustomerEntity();
    when(customerRepository.findById(anyLong())).thenReturn(customerOpt);
    customerService.getCustomerById(1L);
    Mockito.verify(customerRepository, times(1)).findById(1L);

  }

  @Test
  void saveCustomer_ifUserFound_thenCallSaveMethod() throws IOException {

    MockMultipartFile multipartFile = getMockMultipartFile();

    String customer = "{\n" + " \"customerId\": 11,\n" + "  \"name\":\"mr\",\n" + "  \"surname\":\"papanato\",\n" + "  \"photoUrl\": \"s3://\"\n" + "}";
    String loggerUser = "user";

    CustomerFlowResult flowResult = CustomerFlowResult.builder()
        .customer(createDomainCustomer())
        .photoUrl("s3://file/url")
        .user(createUserEntity())
        .build();
    when(customerFlowConverter.process(loggerUser, multipartFile, customer)).thenReturn(flowResult);
    when(customerRepository.findByIdAndStatus(any(), any())).thenReturn(Optional.empty());
    when(customerRepository.save(any())).thenReturn(createCustomerEntity().get());
    customerService.save(multipartFile, customer, loggerUser);

    Mockito.verify(customerRepository, times(1)).save(any());
  }

  private MockMultipartFile getMockMultipartFile() {
    MockMultipartFile multipartFile
        = new MockMultipartFile(
        "file",
        "hello.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Hello, World!".getBytes()
    );
    return multipartFile;
  }

  private Optional<CustomerEntity> createCustomerEntity() {
    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setSurname("testsurname");
    customerEntity.setName("testname");
    customerEntity.setPhotoUrl("s3://bucket/photo.jpg");
    UserEntity user = createUserEntity();
    customerEntity.setUserEntity(user);
    return Optional.of(customerEntity);
  }

  private Customer createDomainCustomer() {

    return Customer.builder()
        .loggedUserName("user")
        .photoUrl("s3://bucket/photo.jpg")
        .name("name")
        .surname("surname")
        .customerId(1L)
        .build();
  }
}

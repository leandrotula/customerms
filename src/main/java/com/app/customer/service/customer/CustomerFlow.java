package com.app.customer.service.customer;

import com.app.customer.service.domain.CustomerFlowResult;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerFlow {

  CustomerFlowResult process(String loggerUsername, MultipartFile multipartFile, String customer);
}

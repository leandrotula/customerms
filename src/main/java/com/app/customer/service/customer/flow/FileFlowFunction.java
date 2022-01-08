package com.app.customer.service.customer.flow;

import com.app.customer.exception.StorageException;
import com.app.customer.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileFlowFunction implements Function<MultipartFile, String> {

  private final StorageService storageService;

  @Override
  public String apply(MultipartFile file) {

    if (file == null) {
      return "";
    }
    try {
      String version = storageService.uploadFile(file);
      log.info("File successfully uploaded, version {} ", version);
      return storageService.getUrlFile(file.getOriginalFilename());
    } catch (final Exception exception) {
      throw new StorageException(exception);
    }
  }
}

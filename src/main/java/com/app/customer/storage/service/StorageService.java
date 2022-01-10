package com.app.customer.storage.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  String uploadFile(MultipartFile file);

  String getUrlFile(String fileName);
}

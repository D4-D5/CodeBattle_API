package com.example.CodeWar.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileStorageService {
    Map<String, Object> storeFile(MultipartFile file);
}

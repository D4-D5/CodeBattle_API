package com.example.CodeWar.services;

import com.example.CodeWar.exception.FileException;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String location) throws FileException;
}

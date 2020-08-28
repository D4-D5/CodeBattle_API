package com.example.CodeWar.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Service {
    // @Async annotation ensures that the method is executed in a different background thread
    // but not consume the main thread.

    // @Async annotation ensures that the method is executed in a different background thread
    // but not consume the main thread.
    void uploadFile(MultipartFile multipartFile, String location);
}

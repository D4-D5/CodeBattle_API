package com.example.CodeWar.services.implementation;

import com.example.CodeWar.exception.FileException;
import com.example.CodeWar.services.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.CodeWar.app.Constants.*;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Override
    public String storeFile(MultipartFile file, String location) throws FileException {
        if(Objects.isNull(file) || file.isEmpty()){
            return "";
        }
        System.out.println(location);
        // Normalize file name
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileException(FILE_INVALID_PATH);
            }
            //make dir if not already exits
            File folder = new File(location);
            if (!folder.exists()) {
                if(!folder.mkdirs()){
                    throw new FileException(FILE_MKDIR);
                }
            }
            System.out.println("File content tye "+file.getContentType());
            // Copy file to the target location (Replacing existing file with the same name)
            Path path = Paths.get(location + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new FileException(FILE_ERROR);
        }
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/download/")
                .path(fileName)
                .toUriString();
        String fileLocation = new StringBuilder().append(location).append(fileName).toString();
        return fileLocation;
    }

//    public Resource loadFileAsResource(String fileName) {
//        try {
//            Path filePath = Paths.get(FILE_BASE_PATH+fileName).normalize();
//            Resource resource = new UrlResource(filePath.toUri());
//            if(resource.exists()) {
//                return resource;
//            } else {
//                throw new MyFileNotFoundException("File not found " + fileName);
//            }
//        } catch (MalformedURLException ex) {
//            throw new MyFileNotFoundException("File not found " + fileName, ex);
//        }
//    }
}
package com.jnotifier.services.core;

import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.response.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("uploads");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    public String saveFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Cannot store an empty file.");
            }

            // 1. Get the original filename
            String originalFilename = file.getOriginalFilename();

            // 2. Extract the file extension (e.g., ".jpg", ".pdf")
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 3. Generate a unique name using UUID and append the extension
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // 4. Resolve the new path using the unique filename
            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(uniqueFilename))
                    .normalize().toAbsolutePath();

            // Security check
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }

            // Copy the file
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Return the new unique filename so it can be saved to a database or returned to the user
            return uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    public Resource loadFileAsResource(String filename) throws MalformedURLException {
        // Find the file path
        Path filePath = this.rootLocation.resolve(filename).normalize();

        // Convert it to a Spring Resource
        Resource resource = new UrlResource(filePath.toUri());

        // Check if the file exists and is readable
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new GenericException(ApiResponse.error("FILE_NOT_FOUND", "Could not read the file: " + filename));
        }
    }
}
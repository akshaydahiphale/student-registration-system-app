package com.enterprise.studentregistration.util;

import com.enterprise.studentregistration.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Handles storage of uploaded student photos on the local filesystem,
 * under the directory configured by `app.upload.dir`.
 */
@Component
public class FileStorageUtil {

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/jpg", "image/webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Value("${app.upload.dir}")
    private String uploadDir;

    private Path storageLocation() {
        Path location = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(location);
        } catch (IOException e) {
            throw new FileStorageException("Could not create upload directory: " + location, e);
        }
        return location;
    }

    /**
     * Stores the given photo and returns the relative path (to persist on
     * the Student entity) used later to serve/display the image.
     */
    public String storePhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Please select a photo to upload");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new FileStorageException("Only JPG, JPEG, PNG or WEBP images are allowed");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileStorageException("Photo size must not exceed 5MB");
        }

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "photo" : file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        String newFilename = UUID.randomUUID() + extension;

        try (InputStream in = file.getInputStream()) {
            Path target = storageLocation().resolve(newFilename);
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store photo: " + originalFilename, e);
        }

        return newFilename;
    }

    public void deletePhoto(String filename) {
        if (filename == null || filename.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(storageLocation().resolve(filename));
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete photo: " + filename, e);
        }
    }
}

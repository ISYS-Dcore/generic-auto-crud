package io.github.isysdcore.genericAutoCrud.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

public class FileStorageService {

    /**
     * Save a multipart file to the configured path.
     */
    public static String saveFile(MultipartFile file, String storagePath) {
        try {
            Path directory = Paths.get(storagePath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            Path destination = directory.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return destination.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }

    /**
     * Read a file by its name and return its bytes.
     */
    public static byte[] readFile(String fileName, String storagePath) {
        try {
            Path filePath = Paths.get(storagePath).resolve(fileName);
            if (!Files.exists(filePath)) {
                throw new RuntimeException("File not found: " + fileName);
            }
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + fileName, e);
        }
    }

    /**
     * Delete a file by name if it exists.
     */
    public static boolean deleteFile(String fileName, String storagePath) {
        try {
            Path filePath = Paths.get(storagePath).resolve(fileName);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + fileName, e);
        }
    }

    /**
     * Check if the storage path contains a specific file.
     */
    public static boolean fileExists(String fileName, String storagePath) {
        Path filePath = Paths.get(storagePath).resolve(fileName);
        return Files.exists(filePath);
    }
}

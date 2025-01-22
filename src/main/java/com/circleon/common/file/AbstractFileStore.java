package com.circleon.common.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class AbstractFileStore implements FileStore{

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpeg", "jpg", "png");

    @Override
    public boolean deleteFile(String filePath) {

        Path path = Paths.get(getFileDirectory(), filePath);

        try {
            File file = path.toFile();
            return file.exists() && file.delete();
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean isValidFile(MultipartFile file) {
        return file != null && file.getOriginalFilename() != null
                && !file.isEmpty() && !file.getOriginalFilename().isEmpty();
    }

    @Override
    public boolean isAllowedExtension(String filename) {
        String extension = extractExtension(filename);
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    public String createStoreFileName(String originalFilename) {
        String extension = extractExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extension;
    }

    @Override
    public String extractExtension(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}

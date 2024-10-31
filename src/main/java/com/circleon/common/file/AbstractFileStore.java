package com.circleon.common.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public abstract class AbstractFileStore implements FileStore{

    protected abstract String getFileDirectory();

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

    protected String createStoreFileName(String originalFilename) {
        String extension = extractExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extension;
    }

    protected String extractExtension(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}

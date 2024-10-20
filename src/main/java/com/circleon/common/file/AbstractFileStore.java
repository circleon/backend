package com.circleon.common.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public abstract class AbstractFileStore implements FileStore{

    protected abstract String getFileDirectory();

    @Override
    public String storeFile(MultipartFile file){
        try {
            validateFile(file);

            String originalFilename = file.getOriginalFilename();
            String storeFileName = createStoreFileName(originalFilename);

            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            Path uploadPath = Paths.get(getFileDirectory(), currentDate);

            Files.createDirectories(uploadPath);

            file.transferTo(uploadPath.resolve(storeFileName).toFile());

            return currentDate + "/" + storeFileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteFile(Path path) {
        try {
            File file = path.toFile();
            return file.exists() && file.delete();
        }catch (Exception e){
            return false;
        }
    }

    protected void validateFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if(originalFilename == null) {
            throw new RuntimeException("파일 이름이 존재하지 않습니다.");
        }
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

package com.circleon.common.file;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStore {

    String storeFile(MultipartFile file);

    boolean deleteFile(Path path);

    String storeThumbnail(MultipartFile file);
}

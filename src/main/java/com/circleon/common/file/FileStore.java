package com.circleon.common.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


public interface FileStore {

    String getFileDirectory();

    String storeFile(MultipartFile file);

    boolean deleteFile(String filePath);

    String storeThumbnail(MultipartFile file);

    boolean isValidFile(MultipartFile file);

    Resource loadFileAsResource(String filePath);
}

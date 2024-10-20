package com.circleon.domain.circle;

import com.circleon.common.file.AbstractFileStore;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CircleFileStore extends AbstractFileStore {

    @Value("${circle.image.dir}")
    private String circleFileDirectory;

    @Override
    protected String getFileDirectory() {
        return circleFileDirectory;
    }

    @Override
    public String storeThumbnail(MultipartFile file) {
        try {
            validateFile(file);

            String originalFilename = file.getOriginalFilename();
            String storeFileName = "thumb_" + createStoreFileName(originalFilename);

            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            Path uploadPath = Paths.get(getFileDirectory(), currentDate);

            Files.createDirectories(uploadPath);

            Thumbnails.of(file.getInputStream())
                    .size(150, 150)
                    .keepAspectRatio(true)
                    .outputFormat(extractExtension(originalFilename))
                    .toFile(uploadPath.resolve(storeFileName).toFile());

            return currentDate + "/" + storeFileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.circleon.domain.circle;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.common.file.AbstractFileStore;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class CircleFileStore extends AbstractFileStore {

    @Value("${circle.image.dir}")
    private String circleFileDirectory;

    @Override
    public String getFileDirectory() {
        return circleFileDirectory;
    }

    @Override
    public String storeFile(MultipartFile file){
        try {

            if(!isValidFile(file)){
                return null;
            }

            byte[] fileBytes = file.getBytes();

            String originalFilename = file.getOriginalFilename();
            String storeFileName = createStoreFileName(originalFilename);

            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            Path uploadPath = Paths.get(getFileDirectory(), currentDate);

            Files.createDirectories(uploadPath);

            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(fileBytes));

            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            if(width > 800 || height > 800){
                Thumbnails.of(new ByteArrayInputStream(fileBytes))
                        .size(800, 800)
                        .keepAspectRatio(true)
                        .outputFormat(extractExtension(originalFilename))
                        .toFile(uploadPath.resolve(storeFileName).toFile());
            }else{
                Files.write(uploadPath.resolve(storeFileName), fileBytes);
            }

            return currentDate + "/" + storeFileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String storeThumbnail(MultipartFile file) {
        try {
            if(!isValidFile(file)){
                return null;
            }

            byte[] fileBytes = file.getBytes();

            String originalFilename = file.getOriginalFilename();
            String storeFileName = "thumb_" + createStoreFileName(originalFilename);

            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            Path uploadPath = Paths.get(getFileDirectory(), currentDate);

            Files.createDirectories(uploadPath);

            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(fileBytes));

            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            if(width > 150 || height > 150){
                Thumbnails.of(new ByteArrayInputStream(fileBytes))
                        .crop(Positions.CENTER)
                        .size(150, 150)
                        .outputFormat(extractExtension(originalFilename))
                        .toFile(uploadPath.resolve(storeFileName).toFile());
            }else{
                Files.write(uploadPath.resolve(storeFileName), fileBytes);
            }


            return currentDate + "/" + storeFileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Resource loadFileAsResource(String filePath){

        try {
            Path path = Paths.get(getFileDirectory()).resolve(filePath);

            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                throw new CommonException(CommonResponseStatus.FILE_NOT_FOUND);
            }

            return resource;
        }catch (MalformedURLException e){
            throw new RuntimeException("파일 경로가 잘 못 되었습니다.", e);
        }
    }

}

package com.circleon.domain.post;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.common.file.AbstractFileStore;
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
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class PostFileStore extends AbstractFileStore {

    @Value("${circle.image.dir}")
    private String postFileDirectory;

    @Override
    public String getFileDirectory() {
        return postFileDirectory;
    }

    @Override
    public String storeFile(MultipartFile file, Long id){
        try {

            if(!isValidFile(file)){
                return null;
            }

            byte[] fileBytes = file.getBytes();

            String originalFilename = file.getOriginalFilename();
            String storeFileName = createStoreFileName(originalFilename);

            String circleId = String.valueOf(id);
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            Path uploadPath = Paths.get(getFileDirectory(), circleId, currentDate);

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

            return circleId + "/" + currentDate + "/" + storeFileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String storeThumbnail(MultipartFile file, Long id) {
        try {
            if(!isValidFile(file)){
                return null;
            }

            byte[] fileBytes = file.getBytes();

            String originalFilename = file.getOriginalFilename();
            String storeFileName = "thumb_" + createStoreFileName(originalFilename);

            String circleId = String.valueOf(id);
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            Path uploadPath = Paths.get(getFileDirectory(), circleId, currentDate);

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


            return circleId + "/" + currentDate + "/" + storeFileName;
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

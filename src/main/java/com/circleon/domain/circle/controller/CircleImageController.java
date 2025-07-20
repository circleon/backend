package com.circleon.domain.circle.controller;

import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.common.file.FileStore;
import com.circleon.domain.circle.dto.CircleImagesUpdateRequest;
import com.circleon.domain.circle.dto.CircleImagesUpdateResponse;
import com.circleon.domain.circle.service.CircleImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/circles")
public class CircleImageController {

    public static final int CACHE_MAX_AGE = 365;
    private final CircleImageService circleImageService;
    private final FileStore circleFileStore;


    @PutMapping("/{circleId}/images")
    public ResponseEntity<CircleImagesUpdateResponse> updateCircleImages(@Valid @ModelAttribute CircleImagesUpdateRequest circleImagesUpdateRequest,
                                                                         @LoginUser Long userId,
                                                                         @PathVariable Long circleId){

        CircleImagesUpdateResponse circleImagesUpdateResponse = circleImageService.updateCircleImages(userId, circleId, circleImagesUpdateRequest);

        return ResponseEntity.ok(circleImagesUpdateResponse);
    }

    @DeleteMapping("/{circleId}/images")
    public ResponseEntity<SuccessResponse> deleteCircleImages(@LoginUser Long userId,
                                                              @PathVariable Long circleId,
                                                              @RequestParam boolean deleteProfileImg,
                                                              @RequestParam boolean deleteIntroImg){

        circleImageService.deleteCircleImages(userId, circleId, deleteProfileImg, deleteIntroImg);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    @GetMapping("/images/{circleId}/{directory}/{filename}")
    public ResponseEntity<Resource> findImage(@PathVariable Long circleId,
                                              @PathVariable String directory,
                                              @PathVariable String filename){

        String filePath = circleId + "/" + directory + "/" + filename;
        Resource resource = circleImageService.loadImageAsResource(filePath);
        String extension = circleFileStore.extractExtension(filename);

        MediaType mediaType = extension.equals("png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl
                        .maxAge(CACHE_MAX_AGE, TimeUnit.DAYS)
                        .cachePublic()
                        .immutable()
                )
                .body(resource);
    }
}

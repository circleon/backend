package com.circleon.domain.circle.controller;

import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.common.file.FileStore;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.dto.CircleImagesUpdateRequest;
import com.circleon.domain.circle.dto.CircleImagesUpdateResponse;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.service.CircleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/circles")
public class CircleImageController {

    private final CircleService circleService;
    private final FileStore circleFileStore;

    @PutMapping("/{circleId}/images")
    public ResponseEntity<CircleImagesUpdateResponse> updateCircleImages(@Valid @ModelAttribute CircleImagesUpdateRequest circleImagesUpdateRequest,
                                                                         @LoginUser Long userId,
                                                                         @PathVariable Long circleId){

        CircleImagesUpdateResponse circleImagesUpdateResponse = circleService.updateCircleImages(userId, circleId, circleImagesUpdateRequest);

        return ResponseEntity.ok(circleImagesUpdateResponse);
    }

    @DeleteMapping("/{circleId}/images")
    public ResponseEntity<SuccessResponse> deleteCircleImages(@LoginUser Long userId,
                                                              @PathVariable Long circleId,
                                                              @RequestParam boolean deleteProfileImg,
                                                              @RequestParam boolean deleteIntroImg){

        circleService.deleteCircleImages(userId, circleId, deleteProfileImg, deleteIntroImg);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    @GetMapping("/images/{circleId}/{directory}/{filename}")
    public ResponseEntity<Resource> findImage(@PathVariable Long circleId,
                                              @PathVariable String directory,
                                              @PathVariable String filename){

        String filePath = circleId + "/" + directory + "/" + filename;
        Resource resource = circleService.loadImageAsResource(filePath);
        String extension = circleFileStore.extractExtension(filename);

        MediaType mediaType;
        if(extension.equals("png")){
            mediaType = MediaType.IMAGE_PNG;
        }else{
            mediaType = MediaType.IMAGE_JPEG;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    @ExceptionHandler(CircleException.class)
    public ResponseEntity<ErrorResponse> handleCircleException(CircleException e) {

        CircleResponseStatus status = e.getStatus();

        log.error("CircleException: {}", e.getMessage());

        log.error("CircleException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());


        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }
}

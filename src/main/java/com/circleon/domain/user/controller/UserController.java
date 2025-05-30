package com.circleon.domain.user.controller;

import com.circleon.domain.user.service.UserFileStore;
import com.circleon.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    public static final int CACHE_MAX_AGE = 365;
    private final UserService userService;
    private final UserFileStore userFileStore;

    @GetMapping("/image/{directory}/{filename}")
    public ResponseEntity<Resource> findImage(@PathVariable String directory,
                                              @PathVariable String filename){
        String filePath = directory + "/" + filename;
        Resource resource = userService.loadImageAsResource(filePath);
        String extension = userFileStore.extractExtension(filename);
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

package com.circleon.domain.circle.controller;

import com.circleon.common.annotation.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/circles")
public class CircleController {



    @GetMapping("/test")
    public String test(@LoginUser Long userId) {

        log.info("userId = {}", userId);

        return userId.toString();
    }
}

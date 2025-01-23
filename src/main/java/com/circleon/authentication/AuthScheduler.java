package com.circleon.authentication;

import com.circleon.authentication.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class AuthScheduler {

    private final AuthService authService;

//    @Scheduled(initialDelay = 5000)
//    public void cleanUpExpiredRefreshTokens(){
//        try {
//            log.info("Cleaning up expired refresh tokens");
//            authService.deleteExpiredRefreshTokens();
//        }catch (Exception e){
//            log.error("Cleaning up expired refresh tokens fail", e);
//        }
//    }
}

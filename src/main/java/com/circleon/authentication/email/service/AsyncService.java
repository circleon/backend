package com.circleon.authentication.email.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AsyncService {

    @Async("emailTaskExecutor")
    public CompletableFuture<String> testAsync(String email) {
        log.info("Async service 시작 : {}", Thread.currentThread().getName());
        try{
            Thread.sleep(10000);
            email = "aaaa@aaaa";
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }

        log.info("Async service 종료 : {}", Thread.currentThread().getName());

        return CompletableFuture.completedFuture("이메일 테스트 전송 완료: " + email);
    }

}

package com.circleon.authentication.service;

import com.circleon.authentication.dto.RefreshTokenRequest;
import com.circleon.authentication.dto.RefreshTokenResponse;
import com.circleon.authentication.entity.RefreshToken;
import com.circleon.authentication.jwt.JwtUtil;
import com.circleon.authentication.repository.RefreshTokenRepository;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class RefreshTokenConcurrencyTest {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImplTest.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthServiceImpl authServiceImpl;

    @Test
    @DisplayName(value = "리프레쉬 토큰 동시성 체크 낙관적 락 사용")
    void testRefreshTokenWithConcurrency() throws InterruptedException {

        int threadCount = 10;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        User user = userRepository.findByEmail("user1@ajou.ac.kr").orElse(null);

        String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getRole().name());
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getRole().name());

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);


        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken(refreshToken)
                .build();

        for(int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try{
                    RefreshTokenResponse refreshTokenResponse = authServiceImpl.refreshToken(refreshTokenRequest);
                    if(refreshTokenResponse == null) {
                        successCount.incrementAndGet();
                    }else {
                        failureCount.incrementAndGet();
                    }
                }catch (OptimisticLockingFailureException e) {
                    log.info("낙관적 락 충돌 발생! test: {} ", e.getMessage());
                }
                catch (Exception e) {
                    log.warn("Exception: {}", e.getMessage());
                    failureCount.incrementAndGet();
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        log.info("성공한 요청 수: {}", successCount.get());
        log.info("실패한 요청 수: {}", failureCount.get());

    }

}

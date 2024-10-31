package com.circleon.config.dataloader;

import com.circleon.domain.user.entity.Role;
import com.circleon.domain.user.entity.UnivCode;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(1)
public class UserDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if(userRepository.count() == 0) {
            for(int i = 0 ; i < 50; i++){
                String email = "user" + i + "@ajou.ac.kr";
                String password = "user" + i;
                String username = "user" + i;

                UserStatus status = null;
                if(i % 10 == 0){
                    status = UserStatus.DEACTIVATED;
                }else{
                    status = UserStatus.ACTIVE;
                }

                User user = User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .username(username)
                        .univCode(UnivCode.AJOU)
                        .status(status)
                        .role(Role.ROLE_USER)
                        .build();

                userRepository.save(user);
            }
        }
    }
}

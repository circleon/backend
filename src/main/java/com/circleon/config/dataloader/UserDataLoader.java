package com.circleon.config.dataloader;

import com.circleon.domain.user.entity.Role;
import com.circleon.domain.user.entity.UnivCode;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if(userRepository.count() == 0) {
            for(int i = 0 ; i < 10; i++){
                String email = "user" + i + "@ajou.ac.kr";
                String password = "user" + i;
                String username = "user" + i;

                User user = User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .username(username)
                        .univCode(UnivCode.AJOU)
                        .userStatus(UserStatus.ACTIVE)
                        .role(Role.ROLE_USER)
                        .build();

                userRepository.save(user);
            }
        }
    }
}

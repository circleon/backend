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

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(1)
public class UserDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        List<User> users = new ArrayList<>();

        if(userRepository.count() == 0) {
            for(int i = 1 ; i <= 180; i++){
                String email = "user" + i + "@ajou.ac.kr";
                String password = "user" + i;
                String username = "user" + i;


                User user = User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .username(username)
                        .univCode(UnivCode.AJOU)
                        .status(UserStatus.ACTIVE)
                        .role(Role.ROLE_USER)
                        .build();

                users.add(user);

            }

            for(int i = 181 ; i <= 200; i++){
                String email = "user" + i + "@ajou.ac.kr";
                String password = "user" + i;
                String username = "user" + i;


                User user = User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .username(username)
                        .univCode(UnivCode.AJOU)
                        .status(UserStatus.DEACTIVATED)
                        .role(Role.ROLE_USER)
                        .build();
                users.add(user);

            }

            userRepository.saveAll(users);
        }
    }
}

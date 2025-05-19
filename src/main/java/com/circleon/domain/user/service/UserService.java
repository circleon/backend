package com.circleon.domain.user.service;


import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.dto.UserDomain;
import com.circleon.domain.user.dto.UserUpdate;
import com.circleon.domain.user.dto.UserResponse;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse findMeById(Long loginId){
        UserDomain userDomain = userRepository.findByIdAndStatus(loginId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND))
                .toDomain();
        return UserResponse.from(userDomain);
    }

    @Transactional
    public UserResponse updateMe(Long userId, UserUpdate userUpdate){
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND));
        UserDomain userDomain = user.toDomain();
        userDomain.updateUserName(userUpdate.getUsername());
        user.apply(userDomain);
        return UserResponse.from(userDomain);
    }
}

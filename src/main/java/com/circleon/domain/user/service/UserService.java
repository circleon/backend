package com.circleon.domain.user.service;


import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.post.service.SignedUrlManager;
import com.circleon.domain.user.ImageManager;
import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.dto.UserInfo;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ImageManager imageManager;
    private final SignedUrlManager signedUrlManager;

    @Transactional(readOnly = true)
    public UserInfo findMeById(Long loginId){
        UserInfo userInfo = userRepository.findByIdAndStatus(loginId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND))
                .toUserInfo();

        String signedUrl = signedUrlManager.createSignedUrl(userInfo.getProfileImgUrl());
        userInfo.updateProfileImgUrl(signedUrl);
        return userInfo;
    }

    @Transactional
    public UserInfo updateMe(Long userId, String username){
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND));
        UserInfo userInfo = user.toUserInfo();
        userInfo.updateUserName(username);
        user.apply(userInfo);
        return userInfo;
    }

    public void updateImage(Long userId, MultipartFile image){
        String oldPath = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND))
                .toUserInfo()
                .getProfileImgUrl();
        String url = imageManager.saveImage(image, userId);
        saveImageMetaOrRollBack(userId, url);
        imageManager.deleteImage(oldPath);
    }

    private void saveImageMetaOrRollBack(Long userId, String url) {
        try{
            imageManager.updateImageMeta(userId, url);
        } catch (RuntimeException e) {
            imageManager.deleteImage(url);
        }
    }

    public void deleteImage(Long userId){
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND));
        UserInfo userInfo = user.toUserInfo();

        imageManager.updateImageMeta(userId, null);
        imageManager.deleteImage(userInfo.getProfileImgUrl());
    }
}

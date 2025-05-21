package com.circleon.domain.user.service;

import com.circleon.domain.user.UserImageManager;
import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.dto.UserInfo;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserImageManager userImageManager;
    private final UserFileStore userFileStore;

    @Transactional(readOnly = true)
    public UserInfo findMeById(Long loginId){
        UserInfo userInfo = userRepository.findByIdAndStatus(loginId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND))
                .toUserInfo();
        return userImageManager.createSignedUrl(userInfo);
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
        String url = userImageManager.saveImage(image, userId);
        saveImageMetaOrRollBack(userId, url);
        userImageManager.deleteImage(oldPath);
    }

    private void saveImageMetaOrRollBack(Long userId, String url) {
        try{
            userImageManager.updateImageMeta(userId, url);
        } catch (RuntimeException e) {
            userImageManager.deleteImage(url);
        }
    }

    public void deleteImage(Long userId){
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND));
        UserInfo userInfo = user.toUserInfo();
        userImageManager.updateImageMeta(userId, null);
        userImageManager.deleteImage(userInfo.getProfileImgUrl());
    }

    public Resource loadImageAsResource(String filePath, String expires, String signature){
        userImageManager.validateSignedImage(filePath, expires, signature);
        return userFileStore.loadFileAsResource(filePath);
    }
}

package com.circleon.domain.user;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.common.file.SignedUrlManager;
import com.circleon.domain.user.dto.UserInfo;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.repository.UserRepository;
import com.circleon.domain.user.service.UserFileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class UserImageManager {

    private final UserFileStore userFileStore;
    private final UserRepository userRepository;
    private final SignedUrlManager signedUrlManager;

    public UserInfo createSignedUrl(User user){
        UserInfo userInfo = UserInfo.from(user);
        String originUrl = userInfo.getProfileImgUrl();
        if(originUrl != null && !originUrl.isBlank()){
            userInfo.changeImgUrlToSignedUrl(signedUrlManager.createSignedUrl(originUrl));
        }
        return userInfo;
    }

    public String saveImage(MultipartFile file, Long userId) {
        if(!userFileStore.isValidFile(file)) throw new CommonException(CommonResponseStatus.FILE_NOT_FOUND, "이미지 파일이 필수입니다;");
        return userFileStore.storeThumbnail(file, userId);
    }

    @Transactional
    public void updateImageMeta(Long userId, String path){
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND));
        user.updateProfileImgUrl(path);
    }

    public void deleteImage(String path){
        if(path == null || path.isEmpty()){
            throw new CommonException(CommonResponseStatus.FILE_NOT_FOUND, "이미지 파일이 존재하지않습니다");
        }
        userFileStore.deleteFile(path);
    }

    public void validateSignedImage(String filePath, String expires, String signature){
        if(!signedUrlManager.isValidSignedUrl(filePath, expires, signature)){
            throw new CommonException(CommonResponseStatus.FILE_SIGN_INVALID, "이미지 서명 검증 실패");
        }

        if(signedUrlManager.isExpired(expires)){
            throw new CommonException(CommonResponseStatus.FILE_EXPIRE_INVALID, "이미지 서명 유효기간 초과");
        }
    }
}

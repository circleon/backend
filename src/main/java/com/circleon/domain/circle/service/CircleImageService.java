package com.circleon.domain.circle.service;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.common.file.FileStore;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.CircleRole;
import com.circleon.domain.circle.dto.CircleImagesUpdateRequest;
import com.circleon.domain.circle.dto.CircleImagesUpdateResponse;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.repository.MyCircleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class CircleImageService {


    private final FileStore circleFileStore;
    private final MyCircleRepository myCircleRepository;

    @Transactional
    public CircleImagesUpdateResponse updateCircleImages(Long userId, Long circleId, CircleImagesUpdateRequest circleImageUpdateRequest) {

        Circle foundCircle = validatePresidentAccess(userId, circleId).getCircle();

        // 이미지 저장 null값이면 수정 안함

        //프로필 사진
        if(circleFileStore.isValidFile(circleImageUpdateRequest.getProfileImg())) {

            //기존 사진들 삭제
            deleteImg(foundCircle.getProfileImgUrl());

            deleteImg(foundCircle.getThumbnailUrl());

            //저장
            String profileImgUrl = storeImg(circleImageUpdateRequest.getProfileImg(), foundCircle.getId());

            String thumbnailUrl = storeThumbnail(circleImageUpdateRequest.getProfileImg(), foundCircle.getId());

            foundCircle.setProfileImgUrl(profileImgUrl);
            foundCircle.setThumbnailUrl(thumbnailUrl);

        }

        //소개글 사진
        if(circleFileStore.isValidFile(circleImageUpdateRequest.getIntroImg())){

            deleteImg(foundCircle.getIntroImgUrl());

            String introImgUrl = storeImg(circleImageUpdateRequest.getIntroImg(), foundCircle.getId());

            foundCircle.setIntroImgUrl(introImgUrl);

        }

        return CircleImagesUpdateResponse.fromCircle(foundCircle);
    }

    @Transactional
    public void deleteCircleImages(Long userId, Long circleId, boolean deleteProfileImg, boolean deleteIntroImg) {

        //권한 체크
        Circle foundCircle = validatePresidentAccess(userId, circleId).getCircle();

        //이미지 삭제
        if(deleteProfileImg){
            deleteImg(foundCircle.getProfileImgUrl());
            deleteImg(foundCircle.getThumbnailUrl());

            foundCircle.setProfileImgUrl(null);
            foundCircle.setThumbnailUrl(null);
        }

        if(deleteIntroImg){
            deleteImg(foundCircle.getIntroImgUrl());

            foundCircle.setIntroImgUrl(null);
        }
    }

    public Resource loadImageAsResource(String filePath) {
        return circleFileStore.loadFileAsResource(filePath);
    }

    private String storeImg(MultipartFile file, Long circleId) {
        String imgUrl = circleFileStore.storeFile(file, circleId);

        if(imgUrl == null){
            throw new CommonException(CommonResponseStatus.INTERNAL_SERVER_ERROR);
        }
        return imgUrl;
    }

    private String storeThumbnail(MultipartFile file, Long circleId) {
        String thumbnailUrl = circleFileStore.storeThumbnail(file, circleId);
        if(thumbnailUrl == null){
            throw new CommonException(CommonResponseStatus.INTERNAL_SERVER_ERROR);
        }
        return thumbnailUrl;
    }

    private void deleteImg(String imgUrl) {

        if(imgUrl == null){
            return ;
        }

        boolean isDeletedImg = circleFileStore.deleteFile(imgUrl);
        if(!isDeletedImg){
            log.warn("동아리 파일 삭제 실패 {}", imgUrl);
        }
    }

    private MyCircle validatePresidentAccess(Long userId, Long circleId) {

        MyCircle member = myCircleRepository.findJoinedMember(userId, circleId)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBER_NOT_FOUND, "[validatePresidentAccess] 멤버가 아닙니다."));

        if (member.getCircleRole() != CircleRole.PRESIDENT) {
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS);
        }
        return member;
    }
}

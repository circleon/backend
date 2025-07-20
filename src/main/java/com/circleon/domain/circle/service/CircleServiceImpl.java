package com.circleon.domain.circle.service;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.exception.CommonException;
import com.circleon.common.file.FileStore;

import com.circleon.domain.circle.*;
import com.circleon.domain.circle.dto.*;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.repository.CircleRepository;
import com.circleon.domain.circle.repository.MyCircleRepository;
import com.circleon.domain.post.entity.Post;
import com.circleon.domain.post.entity.PostImage;
import com.circleon.domain.post.service.CommentDataService;
import com.circleon.domain.post.service.PostDataService;
import com.circleon.domain.post.service.PostImageDataService;
import com.circleon.domain.schedule.circle.service.CircleScheduleDataService;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.service.UserDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class CircleServiceImpl implements CircleService {

    private final CircleRepository circleRepository;
    private final MyCircleRepository myCircleRepository;
    private final UserDataService userDataService;
    private final FileStore circleFileStore;
    private final FileStore postFileStore;
    private final CircleScheduleDataService circleScheduleDataService;
    private final PostDataService postDataService;
    private final CommentDataService commentDataService;
    private final PostImageDataService postImageDataService;
    private final CircleAuthValidator circleAuthValidator;


    @Transactional
    @Override
    public void createCircle(Long applicantId, CircleCreateRequest circleCreateRequest) {

        //존재하는 유저인지 검증
        User foundUser = userDataService.findByIdAndStatus(applicantId, UserStatus.ACTIVE)
                .orElseThrow(()->new CommonException(CommonResponseStatus.USER_NOT_FOUND));


        Circle savedCircle = circleRepository.save(circleCreateRequest.toCircle(foundUser));

        MyCircle myCircle = MyCircle.builder()
                .circleRole(CircleRole.PRESIDENT)
                .membershipStatus(MembershipStatus.APPROVED)
                .circle(savedCircle)
                .user(foundUser)
                .joinMessage("")
                .build();

        myCircle.initJoinedAt();

        myCircleRepository.save(myCircle);

        //프로필 이미지 유효할때
        if(circleFileStore.isValidFile(circleCreateRequest.getProfileImg())) {

            if(!circleFileStore.isAllowedExtension(circleCreateRequest.getProfileImg().getOriginalFilename())){
                throw new CommonException(CommonResponseStatus.FILE_EXTENSION_INVALID, "[createCircle] 허용되지 않는 확장자");
            }

            //이미지 원본 저장
            String profileImgUrl = storeImg(circleCreateRequest.getProfileImg(), savedCircle.getId());
            //썸네일 저장
            String thumbnailUrl = storeThumbnail(circleCreateRequest.getProfileImg(), savedCircle.getId());

            savedCircle.setProfileImgUrl(profileImgUrl);
            savedCircle.setThumbnailUrl(thumbnailUrl);

        }

        //소개글 이미지 유효할때
        if(circleFileStore.isValidFile(circleCreateRequest.getIntroductionImg())){

            if(!circleFileStore.isAllowedExtension(circleCreateRequest.getIntroductionImg().getOriginalFilename())){
                throw new CommonException(CommonResponseStatus.FILE_EXTENSION_INVALID, "[createCircle] 허용되지 않는 확장자");
            }

            String introImgUrl = storeImg(circleCreateRequest.getIntroductionImg(), savedCircle.getId());
            savedCircle.setIntroImgUrl(introImgUrl);
        }

    }

//    @Cacheable(value = "circles", key = "(#categoryType ?: 'ALL') + ':' + #pageable.getPageNumber()")

    @Transactional
    @Override
    public PaginatedResponse<CircleResponse> findPagedCircles(Pageable pageable, CategoryType categoryType) {

        if(categoryType == null){
            Page<CircleResponse> pagedCircles = circleRepository.findAllByCircleStatus(CircleStatus.ACTIVE, pageable)
                    .map(CircleResponse::fromCircle);

            List<CircleResponse> content = pagedCircles.getContent();
            int currentPageNumber = pagedCircles.getNumber();
            long totalElementCount = pagedCircles.getTotalElements();
            int totalPageCount = pagedCircles.getTotalPages();

            return PaginatedResponse.of(content, currentPageNumber, totalElementCount, totalPageCount);
        }else{
            Page<CircleResponse> pagedCircles = circleRepository.findAllByCategoryTypeAndCircleStatus(categoryType, CircleStatus.ACTIVE, pageable)
                    .map(CircleResponse::fromCircle);

            List<CircleResponse> content = pagedCircles.getContent();
            int currentPageNumber = pagedCircles.getNumber();
            long totalElementCount = pagedCircles.getTotalElements();
            int totalPageCount = pagedCircles.getTotalPages();

            return PaginatedResponse.of(content, currentPageNumber, totalElementCount, totalPageCount);
        }
    }

    @Transactional
    @Override
    public CircleInfoUpdateResponse updateCircleInfo(Long userId, Long circleId, CircleInfoUpdateRequest circleInfoUpdateRequest) {

        // 수정 권한 체크
        Circle foundCircle = circleAuthValidator.validatePresidentAccess(userId, circleId).getCircle();

        //수정
        foundCircle.setName(circleInfoUpdateRequest.getCircleName());
        foundCircle.setCategoryType(circleInfoUpdateRequest.getCategoryType());
        foundCircle.setIntroduction(circleInfoUpdateRequest.getIntroduction());
        foundCircle.setRecruitmentStartDate(circleInfoUpdateRequest.getRecruitmentStartDate());
        foundCircle.setRecruitmentEndDate(circleInfoUpdateRequest.getRecruitmentEndDate());
        foundCircle.setSummary(circleInfoUpdateRequest.getSummary());
        foundCircle.setRecruiting(circleInfoUpdateRequest.isRecruiting());

        return CircleInfoUpdateResponse.fromCircle(foundCircle);
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

    @Transactional
    @Override
    public CircleDetailResponse findCircleDetail(Long userId, Long circleId) {

        //존재하는 써클인지
        Circle foundCircle = circleRepository.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND));


        int memberCount =  myCircleRepository.countJoinedMember(foundCircle.getId());

        //목록 조회시 카운트
        foundCircle.setMemberCount(memberCount);

        //현재 써클에 가입유무 후 가입 -> 역할까지
        Optional<MyCircle> member = myCircleRepository.findJoinedOrPendingMember(userId, circleId);

        return member.map(myCircle -> CircleDetailResponse.fromCircle(foundCircle, myCircle.getMembershipStatus(), myCircle.getCircleRole(), myCircle.getId()))
                .orElseGet(()-> CircleDetailResponse.fromCircle(foundCircle, null, null, null));

    }

    @Transactional
    @Override
    public List<CircleSimpleResponse> findAllCirclesSimple() {

        List<Circle> circles = circleRepository.findAllByCircleStatus(CircleStatus.ACTIVE);

        return circles.stream().map(CircleSimpleResponse::fromCircle).toList();
    }


    @Transactional
    @Override
    public void updateOfficialStatus(Long userId, Long circleId, OfficialStatus officialStatus) {

        //가입 유저인지 체크 + 회장 권한 체크
        MyCircle president = circleAuthValidator.validatePresidentAccess(userId, circleId);

        //공식 인증을 하는데 관리자가 아니면 예외
        if(officialStatus == OfficialStatus.OFFICIAL) {

            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "[updateOfficialStatus] 관리자 권한이 필요합니다.");
        }

        president.getCircle().setOfficialStatus(officialStatus);
    }

    @Transactional
    @Override
    public void deleteCircle(Long userId, Long circleId) {
        Circle circle = circleAuthValidator.validatePresidentAccess(userId, circleId).getCircle();
        circle.delete();
    }

    @Transactional
    @Override
    public void deleteSoftDeletedCircles() {

        Pageable pageable = PageRequest.of(0, 100);

        while (true){

            //동아리 검색하고
            List<Circle> circles = circleRepository
                    .findAllByCircleStatus(CircleStatus.INACTIVE, pageable)
                    .getContent();

            if(circles.isEmpty()){
                return;
            }

            //동아리 일정 삭제
            circleScheduleDataService.deleteAllByCircles(circles);

            //동아리 게시글들 검색 -> 그 안에서 먼저 게시글 이미지와 댓글 삭제 -> 마지막에 게시글 삭제
            deletePostByCircles(circles, pageable);

            //멤버 삭제
            myCircleRepository.deleteAllByCircles(circles);

            //동아리 삭제
            circleRepository.deleteCircles(circles);
        }

    }

    private void deletePostByCircles(List<Circle> circles, Pageable pageable) {
        List<Post> posts = postDataService.findAllByCircleIn(circles, pageable).getContent();
        if(!posts.isEmpty()){

            deleteCommentsByPosts(posts);

            deleteImagesByPosts(posts);

            postDataService.deletePosts(posts);
        }
    }

    private void deleteImagesByPosts(List<Post> posts) {
        List<PostImage> postImages = postImageDataService.findAllByPostIn(posts);

        postImages.forEach(postImage -> {
            if(!postFileStore.deleteFile(postImage.getPostImgUrl())){
                log.error("[deletePostImages] 이미지 삭제 실패 {}" , postImage.getPostImgUrl());
            }
        });

        postImageDataService.deletePostImages(postImages);

    }

    private void deleteCommentsByPosts(List<Post> posts) {
        commentDataService.deleteAllByPosts(posts);
    }

}

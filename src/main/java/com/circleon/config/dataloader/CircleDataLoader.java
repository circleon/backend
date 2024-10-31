package com.circleon.config.dataloader;

import com.circleon.common.CommonConstants;
import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.CircleRole;
import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.repository.CircleRepository;
import com.circleon.domain.circle.repository.MyCircleRepository;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Order(2)
public class CircleDataLoader implements CommandLineRunner {

    private final CircleRepository circleRepository;
    private final MyCircleRepository myCircleRepository;
    private final UserRepository userRepository;


    @Override
    public void run(String... args) throws Exception {

        if(circleRepository.count() == 0) {

            List<User> users = userRepository.findAllByStatus(UserStatus.ACTIVE);

            List<User> filteredUsers = users.stream()
                    .filter(user -> user.getId() % 5 == 0)
                    .toList();

            for(int i = 0 ; i < 10; i++){
                String name = "Circle_" + i;
                String profileImgUrl = CommonConstants.DEFAULT_IMG_URL;
                String ThumbImgUrl = CommonConstants.DEFAULT_THUMB_IMG_URL;
                CircleStatus circleStatus = CircleStatus.ACTIVE;
                CategoryType categoryType = CategoryType.values()[i % CategoryType.values().length];
                String introduction = "안녕하세요. " + "Circle_" + i + " 동아리 입니다.";
                User applicant = filteredUsers.get(i % filteredUsers.size());

                Circle circle = Circle.builder()
                        .name(name)
                        .profileImgUrl(profileImgUrl)
                        .categoryType(categoryType)
                        .introduction(introduction)
                        .applicant(applicant)
                        .thumbnailUrl(ThumbImgUrl)
                        .circleStatus(circleStatus)
                        .build();

                circleRepository.save(circle);

                MyCircle myCircle = MyCircle.builder()
                        .circle(circle)
                        .user(applicant)
                        .circleRole(CircleRole.PRESIDENT)
                        .membershipStatus(MembershipStatus.APPROVED)
                        .build();

                myCircleRepository.save(myCircle);
            }

            List<Circle> Circles = circleRepository.findAllByCircleStatus(CircleStatus.ACTIVE);

            for(int i = 0 ; i < users.size(); i++){


                Circle circle = Circles.get(i % Circles.size());
                User user = users.get(i);

                Long applicantId = circle.getApplicant().getId();
                Long userId = user.getId();

                if(applicantId.equals(userId)){
                    continue;
                }

                CircleRole circleRole;
                if(i % 7 == 0){
                    circleRole = CircleRole.EXECUTIVE;
                }else{
                    circleRole = CircleRole.MEMBER;
                }
                MembershipStatus membershipStatus;
                if(i % 13 == 0){
                    membershipStatus = MembershipStatus.INACTIVE;
                }else{
                    membershipStatus = MembershipStatus.APPROVED;
                }

                MyCircle myCircle = MyCircle.builder()
                        .circleRole(circleRole)
                        .circle(circle)
                        .user(user)
                        .membershipStatus(membershipStatus)
                        .build();

                myCircleRepository.save(myCircle);

            }

        }


    }
}

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
import java.util.Random;

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


            for(int i = 1 ; i <= 100; i++){
                String name = "Circle_" + i;
                String profileImgUrl = null;
                String ThumbImgUrl = null;
                CircleStatus circleStatus = CircleStatus.ACTIVE;
                CategoryType categoryType = CategoryType.values()[(i-1) % CategoryType.values().length];
                String introduction = "안녕하세요. " + "Circle_" + i + " 동아리 입니다.";
                User applicant = users.get((i-1) % users.size());

                if(i >= 90){
                    circleStatus = CircleStatus.INACTIVE;
                }

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

            List<Circle> circles = circleRepository.findAllByCircleStatus(CircleStatus.ACTIVE);

            Random random = new Random();

            //동아리 가입
            for(int i = 0 ; i < users.size(); i++){

                int randomIndex = random.nextInt(circles.size());

                for(int j = 0 ; j < 30; j++){
                    Circle circle = circles.get((j+randomIndex) % circles.size());
                    User user = users.get(i);

                    Long applicantId = circle.getApplicant().getId();
                    Long userId = user.getId();

                    if(applicantId.equals(userId)){
                        continue;
                    }


                    MyCircle myCircle = MyCircle.builder()
                            .circleRole(CircleRole.MEMBER)
                            .circle(circle)
                            .user(user)
                            .membershipStatus(MembershipStatus.APPROVED)
                            .build();

                    myCircleRepository.save(myCircle);
                }

            }

            //동아리 직책 변경
            for(int i = 0; i < circles.size(); i++){

                Circle circle = circles.get(i);

                List<MyCircle> myCircles = myCircleRepository.findAllByCircleAndMembershipStatus(circle, MembershipStatus.APPROVED);

                MyCircle member = myCircles.stream()
                        .filter(myCircle -> myCircle.getCircleRole().equals(CircleRole.MEMBER))
                        .findFirst()
                        .orElse(null);

                if(member == null) continue;
                member.setCircleRole(CircleRole.EXECUTIVE);
                myCircleRepository.save(member);
            }

            //동아리 탈퇴자 만들기
            for(int i = 0; i < circles.size(); i++){

                Circle circle = circles.get(i);

                List<MyCircle> myCircles = myCircleRepository.findAllByCircleAndMembershipStatus(circle, MembershipStatus.APPROVED);

                MyCircle member = myCircles.stream()
                        .filter(myCircle -> myCircle.getCircleRole().equals(CircleRole.MEMBER))
                        .findFirst()
                        .orElse(null);

                if(member == null) continue;
                member.setMembershipStatus(MembershipStatus.INACTIVE);
                myCircleRepository.save(member);
            }

        }


    }
}

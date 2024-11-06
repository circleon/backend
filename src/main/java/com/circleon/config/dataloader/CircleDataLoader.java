package com.circleon.config.dataloader;

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

import java.util.ArrayList;
import java.util.List;
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

            createCircleTestData(users);

            createMyCircleTestData(users);

            updateCircleRoleToExecutive();

            updateMembershipStatusToInactive();

        }


    }

    private void updateMembershipStatusToInactive() {
        List<MyCircle> myCircles = new ArrayList<>();
        List<Circle> foundCircles = circleRepository.findAllByCircleStatus(CircleStatus.ACTIVE);

        for(int i = 0; i < foundCircles.size(); i++){

            Circle circle = foundCircles.get(i);

            List<MyCircle> foundMyCircles = myCircleRepository.findAllByCircleAndMembershipStatus(circle, MembershipStatus.APPROVED);

            MyCircle member = foundMyCircles.stream()
                    .filter(myCircle -> myCircle.getCircleRole().equals(CircleRole.MEMBER))
                    .findFirst()
                    .orElse(null);

            if(member == null) continue;
            member.setMembershipStatus(MembershipStatus.INACTIVE);
            myCircles.add(member);
        }

        myCircleRepository.saveAll(myCircles);
    }

    private void updateCircleRoleToExecutive() {

        List<Circle> foundCircles = circleRepository.findAllByCircleStatus(CircleStatus.ACTIVE);
        List<MyCircle> myCircles = new ArrayList<>();

        for(int i = 0; i < foundCircles.size(); i++){

            Circle circle = foundCircles.get(i);

            List<MyCircle> foundMyCircles = myCircleRepository.findAllByCircleAndMembershipStatus(circle, MembershipStatus.APPROVED);

            MyCircle member = foundMyCircles.stream()
                    .filter(myCircle -> myCircle.getCircleRole().equals(CircleRole.MEMBER))
                    .findFirst()
                    .orElse(null);

            if(member == null) continue;
            member.setCircleRole(CircleRole.EXECUTIVE);
            myCircles.add(member);
        }
        myCircleRepository.saveAll(myCircles);
    }

    private void createMyCircleTestData(List<User> users) {

        List<MyCircle> myCircles = new ArrayList<>();
        List<Circle> foundCircles = circleRepository.findAllByCircleStatus(CircleStatus.ACTIVE);

        Random random = new Random();

        for(int i = 0; i < users.size(); i++){

            int randomIndex = random.nextInt(foundCircles.size());

            for(int j = 0 ; j < 30; j++){
                Circle foundeCircle = foundCircles.get((j+randomIndex) % foundCircles.size());
                User user = users.get(i);

                Long applicantId = foundeCircle.getApplicant().getId();
                Long userId = user.getId();

                if(applicantId.equals(userId)){
                    continue;
                }


                MyCircle myCircle = MyCircle.builder()
                        .circleRole(CircleRole.MEMBER)
                        .circle(foundeCircle)
                        .user(user)
                        .membershipStatus(MembershipStatus.APPROVED)
                        .build();
                myCircles.add(myCircle);
            }

        }
        myCircleRepository.saveAll(myCircles);
    }

    private void createCircleTestData(List<User> users) {

        List<Circle> circles = new ArrayList<>();
        List<MyCircle> myCircles = new ArrayList<>();

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

            circles.add(circle);

            MyCircle myCircle = MyCircle.builder()
                    .circle(circle)
                    .user(applicant)
                    .circleRole(CircleRole.PRESIDENT)
                    .membershipStatus(MembershipStatus.APPROVED)
                    .build();
            myCircles.add(myCircle);
        }
        circleRepository.saveAll(circles);
        myCircleRepository.saveAll(myCircles);
    }
}

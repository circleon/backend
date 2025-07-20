package com.circleon.domain.schedule.circle.service;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.CommonStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.CircleRole;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.service.MyCircleDataService;
import com.circleon.domain.schedule.ScheduleResponseStatus;
import com.circleon.domain.schedule.circle.dto.*;
import com.circleon.domain.schedule.circle.entity.CircleSchedule;
import com.circleon.domain.schedule.circle.repository.CircleScheduleRepository;
import com.circleon.domain.schedule.exception.ScheduleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CircleScheduleServiceImpl implements CircleScheduleService {

    private final CircleScheduleRepository circleScheduleRepository;
    private final MyCircleDataService myCircleDataService;

    @Override
    public CircleScheduleCreateResponse createCircleSchedule(CircleMemberIdentifier circleMemberIdentifier, CircleScheduleCreateRequest circleScheduleCreateRequest) {

        //동아리 원 체크
        MyCircle member = validateMembership(circleMemberIdentifier.getUserId(),
                circleMemberIdentifier.getCircleId(),
                "[createCircleSchedule] 가입하지 않은 동아리입니다.");

        //임원 체크
        validateRoleAtLeastExecutive(member);

        //일정 생성
        CircleSchedule circleSchedule = CircleSchedule.builder()
                .title(circleScheduleCreateRequest.getTitle())
                .content(circleScheduleCreateRequest.getContent())
                .startAt(circleScheduleCreateRequest.getStartAt())
                .endAt(circleScheduleCreateRequest.getEndAt())
                .status(CommonStatus.ACTIVE)
                .circle(member.getCircle())
                .build();

        CircleSchedule savedCircleSchedule = circleScheduleRepository.save(circleSchedule);

        return CircleScheduleCreateResponse.fromCircleSchedule(savedCircleSchedule);
    }

    private void validateRoleAtLeastExecutive(MyCircle member) {
        if(CircleRole.MEMBER == member.getCircleRole()){
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "[createCircleSchedule] 임원이 아닙니다.");
        }
    }


    private MyCircle validateMembership(Long userId, Long circleId, String message) {
        return myCircleDataService.findJoinedMember(userId, circleId)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBERSHIP_NOT_FOUND, message));
    }

    @Override
    public CircleMonthlySchedules findMonthlySchedules(CircleMemberIdentifier circleMemberIdentifier, int year, int month) {

        //동아리 원 체크
        MyCircle member = validateMembership(circleMemberIdentifier.getUserId(),
                circleMemberIdentifier.getCircleId(),
                "[findMonthlySchedules] 가입하지 않은 동아리입니다.");

        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDateTime startAt = yearMonth.atDay(1).atStartOfDay();

        LocalDateTime endAt = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        List<CircleScheduleDetail> circleScheduleDetails = circleScheduleRepository.findSchedulesWithinDateRange(member.getCircle().getId(), startAt, endAt);
        CircleInfo circleInfo = circleScheduleRepository.findCircleInfo(member.getCircle().getId());

        return CircleMonthlySchedules.of(year, month, circleInfo, circleScheduleDetails);
    }

    @Override
    public CircleScheduleUpdateResponse updateCircleSchedule(CircleMemberIdentifier circleMemberIdentifier,
                                                            Long circleScheduleId,
                                                            CircleScheduleUpdateRequest circleScheduleUpdateRequest) {

        //동아리 원 체크
        MyCircle member = validateMembership(circleMemberIdentifier.getUserId(),
                circleMemberIdentifier.getCircleId(),
                "[updateCircleSchedule] 가입하지 않은 동아리입니다.");

        //임원 체크
        validateRoleAtLeastExecutive(member);

        CircleSchedule circleSchedule = circleScheduleRepository.findByIdAndCircleAndStatus(circleScheduleId, member.getCircle(), CommonStatus.ACTIVE)
                .orElseThrow(() -> new ScheduleException(ScheduleResponseStatus.SCHEDULE_NOT_FOUND, "[updateCircleSchedule] 일정이 존재하지 않습니다."));


        circleSchedule.setTitle(circleScheduleUpdateRequest.getTitle());
        circleSchedule.setContent(circleScheduleUpdateRequest.getContent());
        circleSchedule.setStartAt(circleScheduleUpdateRequest.getStartAt());
        circleSchedule.setEndAt(circleScheduleUpdateRequest.getEndAt());

        return CircleScheduleUpdateResponse.fromCircleSchedule(circleSchedule);
    }

    @Override
    public void deleteCircleSchedule(CircleMemberIdentifier circleMemberIdentifier, Long circleScheduleId) {

        //동아리 원 체크
        MyCircle member = validateMembership(circleMemberIdentifier.getUserId(),
                circleMemberIdentifier.getCircleId(),
                "[deleteCircleSchedule] 가입하지 않은 동아리입니다.");

        //임원 체크
        validateRoleAtLeastExecutive(member);

        CircleSchedule circleSchedule = circleScheduleRepository.findByIdAndCircleAndStatus(circleScheduleId, member.getCircle(), CommonStatus.ACTIVE)
                .orElseThrow(() -> new ScheduleException(ScheduleResponseStatus.SCHEDULE_NOT_FOUND, "[deleteCircleSchedule] 일정이 존재하지 않습니다."));

        circleSchedule.setStatus(CommonStatus.INACTIVE);
    }

    @Override
    public CircleScheduleDetail findNextSchedule(CircleMemberIdentifier circleMemberIdentifier) {

        //동아리원 체크
        MyCircle member = validateMembership(circleMemberIdentifier.getUserId(),
                circleMemberIdentifier.getCircleId(),
                "[findUpcomingSchedule] 가입하지 않은 동아리입니다.");

        LocalDateTime now = LocalDateTime.now();

        //1년 이내중에
        CircleSchedule circleSchedule = circleScheduleRepository.findFirstByCircleAndStartAtBetweenOrderByStartAt(member.getCircle(), now, now.plusYears(1))
                .orElseThrow(() -> new ScheduleException(ScheduleResponseStatus.SCHEDULE_NOT_FOUND, "[findUpcomingSchedule] 일정이 존재하지 않습니다."));

        return CircleScheduleDetail.fromCircleSchedule(circleSchedule);
    }

}

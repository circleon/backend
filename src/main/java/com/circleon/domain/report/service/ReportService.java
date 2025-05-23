package com.circleon.domain.report.service;

import com.circleon.domain.circle.service.MyCircleDataService;
import com.circleon.domain.report.ReportHandler;
import com.circleon.domain.report.ReportResponseStatus;
import com.circleon.domain.report.ReportType;
import com.circleon.domain.report.dto.CreateReportCommand;
import com.circleon.domain.report.entity.Report;
import com.circleon.domain.report.exception.ReportException;
import com.circleon.domain.report.repository.ReportRepository;

import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    private final MyCircleDataService myCircleDataService;

    private final UserRepository userRepository;

    private final Map<ReportType, ReportHandler> handlerMap;

    public ReportService(List<ReportHandler> handlers,
                         ReportRepository reportRepository,
                         MyCircleDataService myCircleDataService,
                         UserRepository userRepository) {
        this.handlerMap = handlers.stream().collect(Collectors.toMap(ReportHandler::getType, h -> h));
        this.reportRepository = reportRepository;
        this.myCircleDataService = myCircleDataService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createReport(CreateReportCommand command) {

        //해당 자원이 존재하는 지 확인
        ReportHandler handler = handlerMap.get(command.getTargetType());

        if(handler == null){
            throw new ReportException(ReportResponseStatus.REPORT_TYPE_NOT_FOUND, "[createReport] 신고 가능 대상이 아닙니다.");
        }

        //타입별로
        handler.validateTargetExist(command.getTargetId());

        //가입한 유저 조회
//        MyCircle member = myCircleDataService.findJoinedMember(command.getReporterId(), command.getCircleId())
//                .orElseThrow(() -> new ReportException(ReportResponseStatus.MEMBERSHIP_NOT_FOUND, "[createReport] 동아리원이 아닙니다."));

        User reporter = userRepository.findByIdAndStatus(command.getReporterId(), UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND, "[createReport] 유저가 존재하지 않습니다."));


        //신고 신청
        Report report = Report.builder()
                .targetType(command.getTargetType())
                .targetId(command.getTargetId())
                .reporter(reporter)
                .reason(command.getReason())
                .build();

        reportRepository.save(report);

    }

}

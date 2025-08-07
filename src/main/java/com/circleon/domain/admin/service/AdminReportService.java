package com.circleon.domain.admin.service;

import com.circleon.common.CommonStatus;
import com.circleon.domain.admin.AdminResponseStatus;
import com.circleon.domain.admin.dto.CircleInfo;
import com.circleon.domain.admin.dto.CircleReportResponse;
import com.circleon.domain.admin.dto.PostInfo;
import com.circleon.domain.admin.dto.PostReportResponse;
import com.circleon.domain.admin.dto.ReportFindRequest;
import com.circleon.domain.admin.dto.ReportInfo;
import com.circleon.domain.admin.exception.AdminException;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.repository.CircleRepository;
import com.circleon.domain.post.entity.Post;
import com.circleon.domain.post.repository.PostRepository;
import com.circleon.domain.report.ReportType;
import com.circleon.domain.report.entity.Report;
import com.circleon.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final ReportRepository reportRepository;
    private final CircleRepository circleRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<CircleReportResponse> findCircleReports(ReportFindRequest reportFindRequest, Pageable pageable){

        if(reportFindRequest.type() != ReportType.CIRCLE){
            throw new AdminException(AdminResponseStatus.REPORT_TYPE_INVALID);
        }

        Page<Report> reports =
                reportRepository.findReports(reportFindRequest.type(), reportFindRequest.handled(), pageable);

        List<Long> circleIds = reports.getContent().stream()
                .map(Report::getTargetId)
                .distinct()
                .toList();

        Map<Long, Circle> circleMap = circleRepository
                .findByIdIn(circleIds)
                .stream()
                .collect(Collectors.toMap(Circle::getId, circle -> circle));

        return reports.map(report -> {
            Circle circle = circleMap.get(report.getTargetId());
            CircleInfo circleInfo = circle == null ? CircleInfo.empty() : CircleInfo.from(circle);
            return CircleReportResponse.from(ReportInfo.from(report), circleInfo);
        });
    }

    @Transactional
    public void handleReport(Long reportId){
        Report report = reportRepository.findByIdAndHandled(reportId, false)
                .orElseThrow(
                        () -> new AdminException(
                                AdminResponseStatus.REPORT_NOT_FOUND, "[handleReport] 신고가 존재하지 않습니다.")
                );
        report.handle();
    }

    @Transactional(readOnly = true)
    public Page<PostReportResponse> findPostReports(ReportFindRequest reportFindRequest, Pageable pageable){

        if(reportFindRequest.type() != ReportType.POST){
            throw new AdminException(AdminResponseStatus.REPORT_TYPE_INVALID);
        }

        Page<Report> reports =
                reportRepository.findReports(reportFindRequest.type(), reportFindRequest.handled(), pageable);

        List<Long> postIds = reports.getContent().stream()
                .map(Report::getTargetId)
                .distinct()
                .toList();

        Map<Long, Post> postIdToPost = postRepository.findByIdInAndStatus(postIds, CommonStatus.ACTIVE)
                .stream()
                .collect(Collectors.toUnmodifiableMap(Post::getId, Function.identity()));

        return reports.map(report -> {
            Post post = postIdToPost.get(report.getTargetId());
            PostInfo postInfo = post == null ? PostInfo.empty() : PostInfo.from(post);
            return PostReportResponse.from(ReportInfo.from(report), postInfo);
        });
    }
}

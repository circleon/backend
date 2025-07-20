package com.circleon.domain.report.repository;

import com.circleon.common.SortUtils;
import com.circleon.domain.report.ReportType;
import com.circleon.domain.report.entity.Report;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.circleon.domain.report.entity.QReport.report;
import static com.circleon.domain.user.entity.QUser.user;


@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Report> findReports(ReportType reportType, boolean handled, Pageable pageable) {

        List<Report> reports = jpaQueryFactory.select(report)
                .from(report)
                .join(report.reporter, user).fetchJoin()
                .where(
                        report.targetType.eq(reportType),
                        report.handled.eq(handled)
                )
                .orderBy(SortUtils.getOrderSpecifiers(pageable.getSort(), Report.class, report))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalReport = Optional.ofNullable(
                jpaQueryFactory.select(report.count())
                        .from(report)
                        .join(report.reporter, user)
                        .where(
                                report.targetType.eq(reportType),
                                report.handled.eq(handled)
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(reports, pageable, totalReport);
    }
}

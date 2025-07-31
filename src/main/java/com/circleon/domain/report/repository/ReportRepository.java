package com.circleon.domain.report.repository;

import com.circleon.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {

    Optional<Report> findByIdAndHandled(Long id, boolean handled);
}

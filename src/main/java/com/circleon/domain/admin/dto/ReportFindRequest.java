package com.circleon.domain.admin.dto;

import com.circleon.domain.report.ReportType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record ReportFindRequest(
        ReportType type,
        boolean handled,
        int page,
        int size
) {
    public Pageable toCreatedAtDescPageable(){
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(page, size, sort);
    }
}

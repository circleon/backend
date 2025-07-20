package com.circleon.domain.report.exception;

import com.circleon.domain.report.ReportResponseStatus;
import lombok.Getter;

@Getter
public class ReportException extends RuntimeException {

    private final ReportResponseStatus status;

    public ReportException(ReportResponseStatus status, String message) {

      super(message);
      this.status = status;
    }
}

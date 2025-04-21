package com.circleon.domain.report;

import com.circleon.common.converter.AbstractCommonEnumAttributeConverter;
import com.circleon.common.converter.CommonEnum;
import lombok.Getter;

@Getter
public enum ReportType implements CommonEnum {

    POST("게시글"),
    COMMENT("댓글");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    @jakarta.persistence.Converter(autoApply = true)
    public static class Converter extends AbstractCommonEnumAttributeConverter<ReportType> {
        public Converter() {
            super(ReportType.class);
        }
    }
}

package com.circleon.domain.circle;

import com.circleon.common.converter.AbstractCommonEnumAttributeConverter;
import com.circleon.common.converter.CommonEnum;
import lombok.Getter;

@Getter
public enum OfficialStatus implements CommonEnum {

    OFFICIAL("공식"),
    UNOFFICIAL("비공식"),
    REQUEST("신청");

    private final String description;

    OfficialStatus(String description) {
        this.description = description;
    }

    @jakarta.persistence.Converter(autoApply = true)
    public static class Converter extends AbstractCommonEnumAttributeConverter<OfficialStatus> {
        public Converter() {
            super(OfficialStatus.class);
        }
    }

}

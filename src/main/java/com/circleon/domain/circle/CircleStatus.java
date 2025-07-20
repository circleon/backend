package com.circleon.domain.circle;

import com.circleon.common.converter.AbstractCommonEnumAttributeConverter;
import com.circleon.common.converter.CommonEnum;

import lombok.Getter;

@Getter
public enum CircleStatus implements CommonEnum {

    ACTIVE("활성화"),
    INACTIVE("비활성화"),
    PENDING("대기");

    private final String description;

    CircleStatus(String description) {
        this.description = description;
    }

    @jakarta.persistence.Converter(autoApply = true)
    public static class Converter extends AbstractCommonEnumAttributeConverter<CircleStatus> {
        public Converter() {
            super(CircleStatus.class);
        }
    }
}

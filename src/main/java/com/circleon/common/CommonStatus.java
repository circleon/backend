package com.circleon.common;

import com.circleon.common.converter.AbstractCommonEnumAttributeConverter;
import com.circleon.common.converter.CommonEnum;

import lombok.Getter;

@Getter
public enum CommonStatus implements CommonEnum {

    ACTIVE("활성화"),
    INACTIVE("비활성화");

    private final String description;

    CommonStatus(String description) {
        this.description = description;
    }

    @jakarta.persistence.Converter(autoApply = true)
    public static class Converter extends AbstractCommonEnumAttributeConverter<CommonStatus> {
        public Converter() {
            super(CommonStatus.class);
        }
    }
}

package com.circleon.domain.circle;

import com.circleon.common.converter.AbstractCommonEnumAttributeConverter;
import com.circleon.common.converter.CommonEnum;
import lombok.Getter;

@Getter
public enum CircleRole implements CommonEnum {

    PRESIDENT("회장"),
    EXECUTIVE("임원"),
    MEMBER("동아리원");

    private final String description;

    CircleRole(String description) {
        this.description = description;
    }

    @jakarta.persistence.Converter(autoApply = true)
    public static class Converter extends AbstractCommonEnumAttributeConverter<CircleRole> {
        public Converter() {
            super(CircleRole.class);
        }
    }
}

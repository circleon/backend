package com.circleon.domain.circle;

import com.circleon.common.converter.AbstractCommonEnumAttributeConverter;
import com.circleon.common.converter.CommonEnum;
import lombok.Getter;

@Getter
public enum CircleRole implements CommonEnum {

    PRESIDENT("회장", 1),
    EXECUTIVE("임원", 2),
    MEMBER("동아리원", 3);

    private final String description;
    private final int orderPriority;

    CircleRole(String description, int orderPriority) {
        this.description = description;
        this.orderPriority = orderPriority;
    }

    @jakarta.persistence.Converter(autoApply = true)
    public static class Converter extends AbstractCommonEnumAttributeConverter<CircleRole> {
        public Converter() {
            super(CircleRole.class);
        }
    }
}

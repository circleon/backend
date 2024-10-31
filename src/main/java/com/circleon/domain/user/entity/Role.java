package com.circleon.domain.user.entity;

import com.circleon.common.converter.AbstractCommonEnumAttributeConverter;
import com.circleon.common.converter.CommonEnum;
import jakarta.persistence.Converter;
import lombok.Getter;

@Getter
public enum Role implements CommonEnum {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    @jakarta.persistence.Converter(autoApply = true)
    public static class Converter extends AbstractCommonEnumAttributeConverter<Role>{
        public Converter() {
            super(Role.class);
        }
    }
}

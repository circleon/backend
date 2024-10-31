package com.circleon.domain.user.entity;

import com.circleon.common.converter.AbstractCommonEnumAttributeConverter;
import com.circleon.common.converter.CommonEnum;
import jakarta.persistence.Converter;
import lombok.Getter;

@Getter
public enum UnivCode implements CommonEnum {

    AJOU("아주대학교","ajou.ac.kr");

    private final String description;
    private final String email;

    UnivCode(String description, String email) {
        this.description = description;
        this.email = email;
    }

    @jakarta.persistence.Converter(autoApply = true)
    public static class Converter extends AbstractCommonEnumAttributeConverter<UnivCode>{
        public Converter() {
            super(UnivCode.class);
        }
    }

}

package com.circleon.domain.circle;

import com.circleon.common.converter.AbstractCommonEnumAttributeConverter;
import com.circleon.common.converter.CommonEnum;

import lombok.Getter;

@Getter
public enum CategoryType implements CommonEnum {

    IT_COMPUTER("IT/컴퓨터"),
    SPORTS("스포츠"),
    CULTURE("문화"),
    VOLUNTEER("봉사"),
    ACADEMIC_LIBERAL_ARTS("학술/교양"),
    ENTREPRENEURSHIP("창업"),
    FRIENDSHIP("친목"),
    RELIGION("종교"),
    LANGUAGE("어학"),
    ETC("기타");

    private final String description;

    CategoryType(String description) {
        this.description = description;
    }

    @jakarta.persistence.Converter(autoApply = true)
    public static class Converter extends AbstractCommonEnumAttributeConverter<CategoryType> {
        public Converter() {
            super(CategoryType.class);
        }
    }
}

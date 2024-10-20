package com.circleon.domain.circle;

import lombok.Getter;

public enum CategoryType {

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

    @Getter
    private final String description;

    CategoryType(String description) {
        this.description = description;
    }

}

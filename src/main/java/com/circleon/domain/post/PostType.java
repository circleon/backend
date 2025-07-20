package com.circleon.domain.post;

import com.circleon.common.converter.AbstractCommonEnumAttributeConverter;
import com.circleon.common.converter.CommonEnum;
import jakarta.persistence.Converter;
import lombok.Getter;

@Getter
public enum PostType implements CommonEnum {

    NOTICE("공지사항"),
    POST("게시글");

    private final String description;

    PostType(String description) {
        this.description = description;
    }

    @Converter(autoApply = true)
    public static class Convert extends AbstractCommonEnumAttributeConverter<PostType> {
        public Convert(){
            super(PostType.class);
        }
    }
}

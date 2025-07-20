package com.circleon.common.converter;

import jakarta.persistence.AttributeConverter;
import lombok.Getter;

import java.util.Arrays;

@Getter
public abstract class AbstractCommonEnumAttributeConverter<T extends Enum<T> & CommonEnum> implements AttributeConverter<T, String> {

    private final Class<T> enumClass;

    public AbstractCommonEnumAttributeConverter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        return attribute != null ? attribute.getDescription() : null;
    }

    @Override
    public T convertToEntityAttribute(String dbData) {

        if(dbData == null) {
            return null;
        }

        return Arrays.stream(enumClass.getEnumConstants())
                .filter(enumConstant->enumConstant.getDescription().equals(dbData))
                .findFirst()
                .orElseThrow(()->new IllegalArgumentException("Invalid enum value: " + dbData));
    }
}

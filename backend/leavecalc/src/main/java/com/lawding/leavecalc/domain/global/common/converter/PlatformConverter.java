package com.lawding.leavecalc.domain.global.common.converter;

import com.lawding.leavecalc.domain.global.common.enums.Platform;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PlatformConverter implements AttributeConverter<Platform, String> {
    @Override
    public String convertToDatabaseColumn(Platform platform) {
        return platform == null ? null : platform.getLabel();
    }

    @Override
    public Platform convertToEntityAttribute(String platform) {
        if (platform == null) return null;
        return switch (platform) {
            case "web" -> Platform.WEB;
            case "ios" -> Platform.IOS;
            default -> Platform.OTHER;
        };
    }
}

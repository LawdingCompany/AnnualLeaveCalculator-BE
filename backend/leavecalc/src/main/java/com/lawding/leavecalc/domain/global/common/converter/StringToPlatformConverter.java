package com.lawding.leavecalc.domain.global.common.converter;

import com.lawding.leavecalc.domain.global.common.enums.Platform;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToPlatformConverter implements Converter<String, Platform> {

    @Override
    public Platform convert(String source) {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("platform 값이 비어있습니다");
        }

        String normalized = source.trim().toLowerCase();

        return switch (normalized) {
            case "web" -> Platform.WEB;
            case "ios" -> Platform.IOS;
            case "android" -> Platform.ANDROID;
            // OTHER는 명시적으로 허용하지 않음 (필요하면 추가)
            default -> throw new IllegalArgumentException(
                "유효하지 않은 platform 값입니다: " + source
            );
        };
    }
}

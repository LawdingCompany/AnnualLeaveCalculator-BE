package com.lawding.leavecalc.domain.global.common.converter;

import com.lawding.leavecalc.domain.global.common.enums.Platform;
import org.springframework.stereotype.Component;

@Component
public class StringToPlatformConverter implements org.springframework.core.convert.converter.Converter<String, Platform> {
    @Override public Platform convert(String source) {
        if (source == null) return null;
        String s = source.trim().toLowerCase(java.util.Locale.ROOT);
        return switch (s) {
            case "web" -> Platform.WEB;
            case "ios" -> Platform.IOS;
            default -> Platform.OTHER;
        };
    }
}

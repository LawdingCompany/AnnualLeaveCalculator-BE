package com.lawding.leavecalc.domain.global.common.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EmptyStringToNullConverter implements Converter<String,String> {

    @Override
    public String convert(String source) {
        if(source == null) return null;
        String trimmed=source.trim();
        return trimmed.isEmpty()? null : trimmed;
    }
}

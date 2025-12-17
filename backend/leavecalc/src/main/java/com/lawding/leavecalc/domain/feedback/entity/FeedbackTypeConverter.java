package com.lawding.leavecalc.domain.feedback.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FeedbackTypeConverter implements AttributeConverter<FeedbackType, String> {


    @Override   // Enum -> 한글 (app -> db)
    public String convertToDatabaseColumn(FeedbackType type) {
        if (type == null) {
            return null;
        }
        return type.getLabel();
    }

    @Override   // 한글 -> Enum (db -> app)
    public FeedbackType convertToEntityAttribute(String data) {
        if (data == null) {
            return null;
        }
        return FeedbackType.fromLabel(data);
    }
}

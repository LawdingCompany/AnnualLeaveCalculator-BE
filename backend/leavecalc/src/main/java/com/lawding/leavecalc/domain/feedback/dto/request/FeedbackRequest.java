package com.lawding.leavecalc.domain.feedback.dto.request;

import com.lawding.leavecalc.domain.feedback.entity.FeedbackType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FeedbackRequest(
    @NotNull
    FeedbackType type,
    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 5, max = 1000)
    String content,

    @Email @Size(max = 255)
    String email,
    @Min(1) @Max(5)
    Integer rating,
    @Size(min = 36, max = 36)
    @Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        message = "유효한 UUID 형식이어야 합니다."
    )
    String calculationId
) {

}
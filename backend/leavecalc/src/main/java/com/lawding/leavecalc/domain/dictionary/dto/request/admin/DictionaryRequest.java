package com.lawding.leavecalc.domain.dictionary.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DictionaryRequest(
    @NotNull
    Long categoryId,
    @NotBlank
    @Size(max = 200)
    String question,
    @NotBlank
    String content
) {

}

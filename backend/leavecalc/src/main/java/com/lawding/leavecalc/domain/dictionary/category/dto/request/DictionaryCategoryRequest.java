package com.lawding.leavecalc.domain.dictionary.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DictionaryCategoryRequest(
    @NotBlank
    @Size(max = 20)
    String name
) {
}

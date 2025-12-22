package com.lawding.leavecalc.domain.dictionary.category.dto.response;

import com.lawding.leavecalc.domain.dictionary.category.entity.DictionaryCategory;

public record DictionaryCategoryResponse(Long id, String name) {

    public static DictionaryCategoryResponse from(DictionaryCategory category) {
        return new DictionaryCategoryResponse(category.getId(), category.getName());
    }
}

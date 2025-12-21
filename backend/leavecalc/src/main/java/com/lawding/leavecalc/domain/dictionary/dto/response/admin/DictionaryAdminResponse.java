package com.lawding.leavecalc.domain.dictionary.dto.response.admin;

import com.lawding.leavecalc.domain.dictionary.category.dto.response.DictionaryCategoryResponse;
import com.lawding.leavecalc.domain.dictionary.entity.Dictionary;
import java.time.LocalDateTime;

public record DictionaryAdminResponse(Long id, DictionaryCategoryResponse categoryDto, String question,
                                      String content, boolean deleted, LocalDateTime deletedAt) {

    public static DictionaryAdminResponse from(Dictionary dictionary) {
        return new DictionaryAdminResponse(
            dictionary.getId(),
            DictionaryCategoryResponse.from(dictionary.getCategory()),
            dictionary.getQuestion(),
            dictionary.getContent(),
            dictionary.isDeleted(),
            dictionary.getDeletedAt()
        );
    }
}

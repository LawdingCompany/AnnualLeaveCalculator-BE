package com.lawding.leavecalc.domain.dictionary.dto.response;

import com.lawding.leavecalc.domain.dictionary.category.dto.response.DictionaryCategoryResponse;
import com.lawding.leavecalc.domain.dictionary.entity.Dictionary;

public record DictionaryResponse(Long id, DictionaryCategoryResponse category, String question,
                                 String content) {

    public static DictionaryResponse from(Dictionary dictionary) {
        return new DictionaryResponse(
            dictionary.getId(),
            DictionaryCategoryResponse.from(dictionary.getCategory()),
            dictionary.getQuestion(),
            dictionary.getContent()
        );
    }

}

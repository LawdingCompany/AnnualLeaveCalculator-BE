package com.lawding.dictionary.dto.response;

import com.lawding.dictionary.category.dto.response.DictionaryCategoryResponse;
import com.lawding.dictionary.entity.Dictionary;

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

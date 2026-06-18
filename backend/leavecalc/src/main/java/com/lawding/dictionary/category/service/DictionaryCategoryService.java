package com.lawding.dictionary.category.service;

import com.lawding.dictionary.category.dto.request.DictionaryCategoryRequest;
import com.lawding.dictionary.category.entity.DictionaryCategory;
import java.util.List;

public interface DictionaryCategoryService {

    List<DictionaryCategory> findAllDictionaryCategories();

    void createDictionaryCategory(DictionaryCategoryRequest request);

    void updateDictionaryCategory(Long id, DictionaryCategoryRequest request);

    void deleteDictionaryCategory(Long id);
}

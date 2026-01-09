package com.lawding.leavecalc.domain.dictionary.service;

import com.lawding.leavecalc.domain.dictionary.dto.request.admin.DictionaryRequest;
import com.lawding.leavecalc.domain.dictionary.dto.response.DictionaryResponse;
import com.lawding.leavecalc.domain.dictionary.entity.Dictionary;
import com.lawding.leavecalc.domain.global.common.dto.response.PageResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface DictionaryService {

    List<Dictionary> findAllDictionaries();

    Dictionary findDictionary(Long id);

    void createDictionary(DictionaryRequest request);

    void updateDictionary(Long id, DictionaryRequest request);

    void disableDictionary(Long id);

    void enableDictionary(Long id);

    List<Dictionary> findActiveDictionaries();

    Dictionary findActiveDictionary(Long id);

    Page<Dictionary> searchDictionaries(String keyword, int page, int size);
}
